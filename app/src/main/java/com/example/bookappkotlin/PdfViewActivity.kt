package com.example.bookappkotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bookappkotlin.databinding.ActivityPdfViewBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.storage

class PdfViewActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityPdfViewBinding

    //TAG
    private companion object{
        const val TAG = "PDF_VIEW_TAG"
    }

    //book id
    var bookId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent, it will be used to load book from firebase
        bookId = intent.getStringExtra("bookId")!!
        loadBookDetails()

        //handle click, go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun loadBookDetails() {
        Log.d(TAG, "loadBookDetails: Get Pdf URL from db")
        //1) Get book url using book id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book url
                    val pdfUrl = snapshot.child("url").value
                    Log.d(TAG, "onDataChange: PDF_URL: $pdfUrl")

                    //2) load pdf using url from firebase storage
                    loadBookFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

//    private fun loadBookFromUrl(pdfUrl: String) {
//        Log.d(TAG, "loadBookFromUrl: Get PDF from firebase storage using URL")
//
//        val reference = FirebaseDatabase.getInstance().getReference(pdfUrl)
//        reference.getBytes(Constants.MAX_BYTES_PDF)
//            .addOnSuccessListener { bytes ->
//                Log.d(TAG, "loadBookFromUrl: PDF loaded successfully")
//
//                //load pdf
//                binding.pdfView.fromBytes(bytes)
//                    .swipeHorizontal(false)
//                    .onPageChange { page, pageCount ->
//                        val currentPage = page+1
//                        binding.toolbarSubTitleTv.text = "$currentPage/$pageCount"
//                        Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
//                    }
//                    .onError { t ->
//                        Log.e(TAG, "loadBookFromUrl ${t.message}")
//                    }
//
//                    .onPageError { page, t ->
//                        Log.e(TAG, "loadBookFromUrl ${t.message}")
//
//                    }
//                    .load()
//                binding.progressBar.visibility = View.GONE
//            }
//            .addOnFailureListener { e ->
//                Log.e(TAG, "loadBookFromUrl: Error loading PDF: ${e.message}")
//                binding.progressBar.visibility = View.GONE
//            }
//    }
private fun loadBookFromUrl(pdfUrl: String) {
    Log.d(TAG, "loadBookFromUrl: Get PDF from firebase storage using URL")

    // Get a reference to the storage service, and then get a reference to the file
    val storage = Firebase.storage
    val storageRef = storage.getReferenceFromUrl(pdfUrl)

    // Max size of file you want to download in bytes
    val ONE_MEGABYTE: Long = 1024 * 1024
    storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
        Log.d(TAG, "loadBookFromUrl: PDF loaded successfully")

        //load pdf
        binding.pdfView.fromBytes(bytes)
            .swipeHorizontal(false)
            .onPageChange { page, pageCount ->
                val currentPage = page+1
                binding.toolbarSubTitleTv.text = "$currentPage/$pageCount"
                Log.d(TAG, "loadBookFromUrl: $currentPage/$pageCount")
            }
            .onError { t ->
                Log.e(TAG, "loadBookFromUrl ${t.message}")
            }

            .onPageError { page, t ->
                Log.e(TAG, "loadBookFromUrl ${t.message}")

            }
            .load()
        binding.progressBar.visibility = View.GONE
    }
        .addOnFailureListener { e ->
            Log.e(TAG, "loadBookFromUrl: Error loading PDF: ${e.message}")
            binding.progressBar.visibility = View.GONE
        }
}
}