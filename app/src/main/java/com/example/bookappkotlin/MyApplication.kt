package com.example.bookappkotlin

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        //created a static method to convert timestamp to proper date format, so we can use it everywhere in projec, no noeed to re-write
        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            //format dd/MM/yyyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }

        //function to get pdf size
        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
            val TAG = "PDF_SIZE_TAG"

            //using url we can get file and its metadata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetaData ->
                    Log.d(TAG, "loadPdfSize: got metadata")
                    val bytes = storageMetaData.sizeBytes //get size in bytes
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    //convert bytes to KB/Mb
//                    val kb = bytes / 1024
//                    val mb = kb / 1024
//                    if (mb >= 1) {
//                        sizeTv.text = "${String.format("%.2f", mb)} MB"
//                    } else if (kb >= 1) {
//                        sizeTv.text = "${String.format("%.2f", kb)} KB"
//                    } else {
//                        sizeTv.text = "${String.format("%.2f", bytes)} bytes"
//                    }
                    //convert bytes to KB/Mb
                    val kb = bytes / 1024
                    val mb = kb / 1024
                    if (mb >= 1) {
                        sizeTv.text = "${String.format("%.2f", mb.toFloat())} MB"
                    } else if (kb >= 1) {
                        sizeTv.text = "${String.format("%.2f", kb.toFloat())} KB"
                    } else {
                        sizeTv.text = "${String.format("%.2f", bytes.toFloat())} bytes"
                    }
                }
                .addOnFailureListener { e ->
                    //failed to get metadata
                    Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${e.message}")
                }

        }

        fun loadPdfFromUrlSinglePage(
            pdfUrl: String,
            pdfTitle: String,
            pdfView: PDFView,
            progressBar: ProgressBar,
            pagesTv: TextView?
        ) {
            val TAG = "PDF_THUMBNNAIL_TAG"
            //using url we can get file and its metadata from firebase storage
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constants.MAX_BYTES_PDF)
                .addOnSuccessListener { bytes ->


                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    //SET TO PDFVIEW
//                    pdfView.fromBytes(bytes)
//                        .pages(0) //show only first page
//                        .spacing(0) //spacing between pages
//                        .swipeHorizontal(false) //horizontal swipe
//                        .enableSwipe(false)
//                        .onError{ t ->
//                            progressBar.visibility = View.INVISIBLE
//                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
//                        }
//                        .onPageError{ page, t ->
//                            progressBar.visibility = View.INVISIBLE
//                            Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
//                        }
//                        .onLoad{ nbPages ->
//                            Log.d(TAG, "loadPdfFromUrlSinglePage: Page: $nbPages")
//                            progressBar.visibility = View.INVISIBLE
//                            //if pagesTv param is not null then set page numbers
//                            if (pagesTv != null) {
//                                pagesTv.text = "$nbPages"
//                            }
//                        }
//                        .load()
                    pdfView.fromBytes(bytes)
                        .pages(0) //show only first page
                        .spacing(0) //spacing between pages
                        .swipeHorizontal(false) //horizontal swipe
                        .enableSwipe(false)
                        .onError { t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(
                                TAG,
                                "loadPdfFromUrlSinglePage: Error loading PDF: ${t.message}",
                                t
                            )
                        }
                        .onPageError { page, t ->
                            progressBar.visibility = View.INVISIBLE
                            Log.d(
                                TAG,
                                "loadPdfFromUrlSinglePage: Error loading page $page: ${t.message}",
                                t
                            )
                        }
                        .onLoad { nbPages ->
                            Log.d(TAG, "loadPdfFromUrlSinglePage: Page: $nbPages")
                            progressBar.visibility = View.INVISIBLE
                            //if pagesTv param is not null then set page numbers
                            if (pagesTv != null) {
                                pagesTv.text = "$nbPages"
                            }
                        }
                        .load()
                }
                .addOnFailureListener { e ->
                    //failed to get metadata
                    Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${e.message}")
                }


        }

        fun loadCategory(categoryId: String, categoryTv: TextView) {
            //load category using category id from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Categories")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get category
                        val category = "" + snapshot.child("category").value
                        //set to categoryTv
                        categoryTv.text = category
                    }

                    override fun onCancelled(error: DatabaseError) {
                        //failed to get category
                        categoryTv.text = "Category"
                    }
                })
        }

        fun deleteBook(context: Context, bookId: String, bookUrl: String, bookTitle: String) {
            //param details
            //1) context, used when require e.g. for progressdialog, toast
            //2) bookId, to delete book from db
            //3) bookUrl, delete book from firebase storage
            //4) bookTitle, show in dialog etc

            val TAG = "DELETE_BOOK_TAG"

            Log.d(TAG, "deleteBook: deleting...")

            //progress dialog
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Deleting $bookTitle...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            Log.d(TAG, "deleteBook: Deleting from storage...")
            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
            storageReference.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "deleteBook: Deleted from storage")
                    Log.d(TAG, "deleteBook: Deleting from db now...")

                    val ref = FirebaseDatabase.getInstance().getReference("Books")
                    ref.child(bookId)
                        .removeValue()
                        .addOnSuccessListener {
                            //deleted from db

                            progressDialog.dismiss()
                            Toast.makeText(context, "Deleted successfully...", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "deleteBook: Deleted from db")
                        }
                        .addOnFailureListener { e ->
                            //failed to delete from db
                            progressDialog.dismiss()
                            Log.d(TAG, "deleteBook: Failed to delete from db due to ${e.message}")

                            Toast.makeText(context, "Failed to delete from db due to ${e.message}", Toast.LENGTH_SHORT).show()
                        }

                }

                .addOnFailureListener { e ->
                    //failed to delete from storage
                    Log.d(TAG, "deleteBook: Failed to delete from storage due to ${e.message}")
                    Toast.makeText(context, "Failed to delete from due to ${e.message}", Toast.LENGTH_SHORT).show()
                }


        }
    }


}