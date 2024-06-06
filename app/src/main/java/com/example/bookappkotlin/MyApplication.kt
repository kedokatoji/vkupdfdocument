package com.example.bookappkotlin

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object {
        //created a static method to convert timestamp to proper date format, so we can use it everywhere in project, no need to rewrite agin
        fun formatTimeStamp(timestamp: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp
            //format dd/MM/yyyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }
    }

    //function to get pdf size
    fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv: TextView) {
        val Tag = "PDF_SIZE_TAG"

        //using url we can get file and its metadata from firebase storage
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        ref.metadata
            .addOnSuccessListener { storageMetaData ->
                Log.d(Tag, "loadPdfSize: got metadata")
                val bytes = storageMetaData.sizeBytes.toDouble()
                Log.d(Tag, "loadPdfSize: Size Bytes: $bytes")

                //convert bytes to KB/Mb
                val kb = bytes / 1024
                val mb = kb / 1024
                if (mb >= 1) {
                    sizeTv.text = String.format("%.2f MB", mb)
                } else if (kb >= 1) {
                    sizeTv.text = String.format("%.2f KB", kb)
                } else {
                    sizeTv.text = String.format("%.2f Bytes", bytes)
                }
            }
            .addOnFailureListener { e ->
                Log.d(Tag, "loadPdfSize: failed to get metadata: ${e.message}")
            }
    }

    //    fun loadPdfFromUrlSinglePage(
//        pdfUrl: String,
//        pdfTitle: String,
//        pdfView: PDFView,
//        progressBar: ProgressBar,
//        pagesTv: TextView?
//    ){
//
//        val Tag = "PDF_THUMBNNAIL_TAG"
//        //using url we can get file and its metadata from firebase storage
//        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
//        ref.getBytes(Constants.MAX_BYTES_PDF)
//            .addOnSuccessListener { storageMetaData ->
//
//                val bytes = storageMetaData.sizeBytes.toDouble()
//                Log.d(Tag, "loadPdfSize: Size Bytes: $bytes")
//
//                //set to pdfview
//                pdfView.fromBytes(bytes)
//                    .pages(0) //show only first page
//                    .spacing(0)
//                    .swipeHorizontal(false)
//                    .enableSwipe(false)
//                    .onError { t ->
//                        progressBar.visibility = View.INVISIBLE
//                        Log.d(Tag, "LoadPDfFromUrlSinglePage: ${t.message}")
//                    }
//                    .onPageError { page, t ->
//                        progressBar.visibility = View.INVISIBLE
//                        Log.d(Tag, "LoadPDfFromUrlSinglePage: ${t.message}")
//                    }
//                    .onLoad { nbPages ->
//                        progressBar.visibility = View.INVISIBLE
//
//                        //if pagesTv param is not null then set page numbers
//                        if (pagesTv != null) {
//                            pagesTv.text = "$nbPages"
//                        }
//                    }
//            }
//            .addOnFailureListener { e ->
//                Log.d(Tag, "loadPdfSize: failed to get metadata: ${e.message}")
//            }
//
//    }
    fun loadPdfFromUrlSinglePage(
        pdfUrl: String,
        pdfTitle: String,
        pdfView: PDFView,
        progressBar: ProgressBar,
        pagesTv: TextView?
    ) {
        val Tag = "PDF_THUMBNNAIL_TAG"
        //using url we can get file and its metadata from firebase storage
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        var sizeBytes: Long = 0
        ref.metadata
            .addOnSuccessListener { storageMetaData ->
                sizeBytes = storageMetaData.sizeBytes
                Log.d(Tag, "loadPdfSize: Size Bytes: $sizeBytes")
            }
            .addOnFailureListener { e ->
                Log.d(Tag, "loadPdfSize: failed to get metadata: ${e.message}")
            }

        ref.getBytes(Constants.MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                //set to pdfview
                pdfView.fromBytes(bytes)
                    .pages(0) //show only first page
                    .spacing(0)
                    .swipeHorizontal(false)
                    .enableSwipe(false)
                    .onError { t ->
                        progressBar.visibility = View.INVISIBLE
                        Log.d(Tag, "LoadPDfFromUrlSinglePage: ${t.message}")
                    }
                    .onPageError { page, t ->
                        progressBar.visibility = View.INVISIBLE
                        Log.d(Tag, "LoadPDfFromUrlSinglePage: ${t.message}")
                    }
                    .onLoad { nbPages ->
                        Log.d(Tag, "loadPdfFromUrlSinglePage: Pages: $nbPages")
                        progressBar.visibility = View.INVISIBLE

                        //if pagesTv param is not null then set page numbers
                        if (pagesTv != null) {
                            pagesTv.text = "$nbPages"
                        }
                    }
                    .load()
            }
            .addOnFailureListener { e ->
                Log.d(Tag, "loadPdfSize: failed to get metadata: ${e.message}")
            }


    }

    fun loadCategory(categoryId: String, categoryTv: TextView) {
        //load category using category id from firebase
        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("categories")
        ref.child(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get category
                    val category = snapshot.getValue(String::class.java)
                    //set to categoryTv
                    categoryTv.text = category
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TAG", "loadCategory: ${error.message}")
                }
            })
    }


}