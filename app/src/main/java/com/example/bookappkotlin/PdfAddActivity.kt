//package com.example.bookappkotlin
//
//import android.app.AlertDialog
//import android.app.ProgressDialog
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.ActivityResultCallback
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import com.example.bookappkotlin.databinding.ActivityPdfAddBinding
//import com.google.android.gms.tasks.Task
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.storage.FirebaseStorage
//
//class PdfAddActivity : AppCompatActivity() {
//
//
//    //setup view binding activity_pdf_add --> ActivityPdfAddBinding
//    private lateinit var binding: ActivityPdfAddBinding
//
//    //firebase auth
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    //progress dialog (show while uploading pdf)
//    private lateinit var progressDialog: ProgressDialog
//
//    //arrayllist to hold pdf categories
//    private lateinit var categoryArrayList: ArrayList<ModelCategory>
//
//    //uri of picked pdf
//    private var pdfUri: Uri? = null
//
//    //Tag
//    private val TAG = "PDF_ADD_TAG"
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPdfAddBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        //init firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()
//        loadPdfCategories()
//
//        //setup progress    dialog
//        progressDialog = ProgressDialog(this)
//        progressDialog.setTitle("Please wait")
//        progressDialog.setCanceledOnTouchOutside(false)
//
//        //hanlde click go back
//        binding.backBtn.setOnClickListener {
//            onBackPressed()
//        }
//
//        //handle click, show category pick dialog
//        binding.categoryTv.setOnClickListener {
//            categoryPickDialog()
//        }
//
//        //handle click, pick pdf file
//        binding.adttachPdfBtn.setOnClickListener {
//            pdfPickIntent()
//        }
//
//        //handle click, start uploading pdf/book
//        binding.submitBtn.setOnClickListener {
//            validateDate()
//        }
//    }
//
//    private var title = ""
//    private var description = ""
//    private var category = ""
//    private fun validateDate() {
//        //setop 1: Validate data
//        Log.d(TAG, "validateDate: Validating data")
//        //get data
//        title = binding.titleEt.text.toString().trim()
//        description = binding.descriptionEt.text.toString().trim()
//        category = binding.categoryTv.text.toString().trim()
//
//        //validate data
//        if (title.isEmpty()) {
//            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show()
//
//        } else if (description.isEmpty()) {
//            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
//        } else if (category.isEmpty()) {
//            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show()
//        } else if (pdfUri == null) {
//            Toast.makeText(this, "Pick Pdf...", Toast.LENGTH_SHORT).show()
//        } else {
//            //data validated begin upload
//            uploadPdfToStroage()
//        }
//    }
//
//    private fun uploadPdfToStroage() {
//        //step 2: upload pdf to firebase storage
//        Log.d(TAG, "uploadPdfToStrorage: uploading pdf to storage")
//
//        //show progress dialog
//        progressDialog.setMessage("Uploading pdf...")
//        progressDialog.show()
//
//        //timestamp
//        val timestamp = System.currentTimeMillis()
//
//
//        //path of pdf in firebase stroage
//        val filePathAndName = "Books/$timestamp"
//
//        //storage reference
//        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
//        storageReference.putFile(pdfUri!!)
//            .addOnCanceledListener {
//                Log.d(TAG, "uploadPdfToStroage: pdf upload cancelled")
//                //step 3: get url of uploaded pdf
//                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
//                while (!uriTask.isSuccessful);
//                val uploadPdfUrl = "${uriTask.result}"
//
//                uploadPdfInfotoDb(uploadedPdfUrl, timestamp)
//            }
//
//
//            .addOnFailureListener { e ->
//                Log.d(TAG, "uploadPdfToStroage: failed to upload due to ${e.message}")
//                progressDialog.dismiss()
//                Toast.makeText(this, "Failed to upload pdf", Toast.LENGTH_SHORT).show()
//            }
//
//    }
//
//    private fun uploadPdfInfotoDb(uploadedPdfUrl: Any, timestamp: Long) {
//        //step 4: upload pdf info to firebase database
//        Log.d(TAG, "uploadPdfInfotoDb: uploading pdf info to db")
//        progressDialog.setMessage("Uploading pdf info...")
//
//        val uid = firebaseAuth.uid
//
//        //setup data to upload
//        val hashMap: HashMap<String, Any> = HashMap()
//        hashMap["uid"] = "$uid"
//        hashMap["id"] = "$timestamp"
//        hashMap["title"] = "$title"
//        hashMap["description"] = "$description"
//        hashMap["category"] = "$category"
//        hashMap["url"] = "$uploadedPdfUrl"
//        hashMap["timestamp"] = timestamp
//        hashMap["downloadsCount"] = 0
//
//        //db reference DB > Book > Book ID > Book info
//        val ref = FirebaseDatabase.getInstance().getReference("Books")
//        ref.child("$timestamp")
//            .setValue(hashMap)
//            .addOnSuccessListener {
//                //pdf info uploaded
//                Log.d(TAG, "uploadPdfInfotoDb: Pdf uploaded")
//                progressDialog.dismiss()
//                Toast.makeText(this, "Pdf uploaded", Toast.LENGTH_SHORT).show()
//                pdfUri = null
//            }
//            .addOnFailureListener { e ->
//                progressDialog.dismiss()
//                Toast.makeText(this, "Failed to upload pdf info", Toast.LENGTH_SHORT).show()
//                Log.d(TAG, "uploadPdfInfotoDb: Failed to upload pdf info due to ${e.message}")
//            }
//    }
//
//}
//
//private fun loadPdfCategories() {
//    Log.d(TAG, "loadPdfCategories: Loading pdf categories")
//    categoryArrayList = ArrayList()
//
//    //db reference to load categories DF > Categories
//    val ref = FirebaseDatabase.getInstance().getReference("Categories")
//    ref.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onDataChange(snapshot: DataSnapshot) {
//            //clear list before adding data
//            categoryArrayList.clear()
//            for (ds in snapshot.children) {
//                //get data
//                val model = ds.getValue(ModelCategory::class.java)
//                //add to list
//                categoryArrayList.add(model!!)
//                Log.d(TAG, "onDataChange: ${model.category} ")
//            }
//
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//
//        }
//    })
//}
//
//private var selectedCategoryID = ""
//private var selectedCategoryTitle = ""
//
//private fun categoryPickDialog() {
//    Log.d(TAG, "categoryPickDialog: Showing category pick dialog")
//    //list of categories to display in dialog
//    val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
//    for (i in categoryArrayList.indices) {
//        categoriesArray[i] = categoryArrayList[i].category
//    }
//
//    //alert dialog
//    val builder = AlertDialog.Builder(this)
//    builder.setTitle("Pick Category")
//        .setItems(categoriesArray) { dialog, which ->
//            //handle item click
//            //get clicked item
//            selectedCategoryTitle = categoryArrayList[which].category
//            selectedCategoryID = categoryArrayList[which].id
//            //set category to textview
//            binding.categoryTv.text = selectedCategoryTitle
//
//            Log.d(TAG, "categoryPickDialog: Selected ID: $selectedCategoryID")
//            Log.d(TAG, "categoryPickDialog: Selected Category: $selectedCategoryTitle")
//
//
//        }
//        .show()
//}
//
//private fun pdfPickIntent() {
//    Log.d(TAG, "pdfPickIntent: Picking pdf file")
//    //pick pdf file
//    val intent = Intent()
//    intent.type = "application/pdf"
//    intent.action = Intent.ACTION_GET_CONTENT
//    pdfActivityResultLauncher.launch(intent)
//
//}
//
//val pdfActivityResultLauncher = registerForActivityResult(
//    ActivityResultContracts.StartActivityForResult(),
//    ActivityResultCallback<ActivityResult> { result ->
//        if (result.resultCode == RESULT_OK) {
//            Log.d(TAG, "PDF Picked")
//            pdfUri = result.data!!.data
//        } else {
//            Log.d(TAG, "PDF Pick Cancelled")
//            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
//
//        }
//        )
//    }
package com.example.bookappkotlin

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.bookappkotlin.databinding.ActivityPdfAddBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfAddActivity : AppCompatActivity() {

    //setup view binding activity_pdf_add --> ActivityPdfAddBinding
    private lateinit var binding: ActivityPdfAddBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress dialog (show while uploading pdf)
    private lateinit var progressDialog: ProgressDialog

    //arrayllist to hold pdf categories
    private var categoryArrayList: ArrayList<ModelCategory>? = null

    //uri of picked pdf
    private var pdfUri: Uri? = null

    //Tag
    private val TAG = "PDF_ADD_TAG"

    // Declare RESULT_OK
    private val RESULT_OK = Activity.RESULT_OK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        //setup progress    dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        //hanlde click go back
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click, show category pick dialog
        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        //handle click, pick pdf file
        binding.adttachPdfBtn.setOnClickListener {
            pdfPickIntent()
        }

        //handle click, start uploading pdf/book
        binding.submitBtn.setOnClickListener {
            validateDate()
        }
    }

    private var title = ""
    private var description = ""
    private var category = ""
    private fun validateDate() {
        //setop 1: Validate data
        Log.d(TAG, "validateDate: Validating data")
        //get data
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        //validate data
        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Title...", Toast.LENGTH_SHORT).show()

        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter Description...", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Pick Category...", Toast.LENGTH_SHORT).show()
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick Pdf...", Toast.LENGTH_SHORT).show()
        } else {
            //data validated begin upload
            uploadPdfToStroage()
        }
    }

    private fun uploadPdfToStroage() {
        //step 2: upload pdf to firebase storage
        Log.d(TAG, "uploadPdfToStrorage: uploading pdf to storage")

        //show progress dialog
        progressDialog.setMessage("Uploading pdf...")
        progressDialog.show()

        //timestamp
        val timestamp = System.currentTimeMillis()

        //path of pdf in firebase stroage
        val filePathAndName = "Books/$timestamp"

        //storage reference
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
//        storageReference.putFile(pdfUri!!)
//            .addOnCanceledListener {
//                Log.d(TAG, "uploadPdfToStroage: pdf upload cancelled")
//                //step 3: get url of uploaded pdf
//                val uriTask: Task<Uri> = it.storage.downloadUrl
//                while (!uriTask.isSuccessful);
//                val uploadedPdfUrl = "${uriTask.result}"
//                uploadPdfInfotoDb(uploadedPdfUrl, timestamp)
//            }
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"
                uploadPdfInfotoDb(uploadedPdfUrl, timestamp)
            }

            .addOnFailureListener { e ->
                Log.d(TAG, "uploadPdfToStroage: failed to upload due to ${e.message}")
                progressDialog.dismiss()
                Toast.makeText(this@PdfAddActivity, "Failed to upload pdf", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfotoDb(uploadedPdfUrl: Any, timestamp: Long) {
        //step 4: upload pdf info to firebase database
        Log.d(TAG, "uploadPdfInfotoDb: uploading pdf info to db")
        progressDialog.setMessage("Uploading pdf info...")

        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["category"] = "$category"
        hashMap["url"] = "$uploadedPdfUrl"
        hashMap["timestamp"] = timestamp
        hashMap["downloadsCount"] = 0

        //db reference DB > Book > Book ID > Book info
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                //pdf info uploaded
                Log.d(TAG, "uploadPdfInfotoDb: Pdf uploaded")
                progressDialog.dismiss()
                Toast.makeText(this@PdfAddActivity, "Pdf uploaded", Toast.LENGTH_SHORT).show()
                pdfUri = null
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(this@PdfAddActivity, "Failed to upload pdf info", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "uploadPdfInfotoDb: Failed to upload pdf info due to ${e.message}")
            }
    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories")
        categoryArrayList = ArrayList()

        //db reference to load categories DF > Categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                categoryArrayList!!.clear()
                for (ds in snapshot.children) {
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to list
                    categoryArrayList!!.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category} ")
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private var selectedCategoryID = ""
    private var selectedCategoryTitle = ""

    private fun categoryPickDialog() {
        Log.d(TAG, "categoryPickDialog: Showing category pick dialog")
        //list of categories to display in dialog
        val categoriesArray = arrayOfNulls<String>(categoryArrayList!!.size)
        for (i in categoryArrayList!!.indices) {
            categoriesArray[i] = categoryArrayList!![i].category
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray) { dialog, which ->
                //handle item click
                //get clicked item
                selectedCategoryTitle = categoryArrayList!![which].category
                selectedCategoryID = categoryArrayList!![which].id
                //set category to textview
                binding.categoryTv.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: Selected ID: $selectedCategoryID")
                Log.d(TAG, "categoryPickDialog: Selected Category: $selectedCategoryTitle")
            }
            .show()
    }

    private fun pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: Picking pdf file")
        //pick pdf file
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "PDF Picked")
                pdfUri = result.data!!.data
            } else {
                Log.d(TAG, "PDF Pick Cancelled")
                Toast.makeText(this@PdfAddActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}