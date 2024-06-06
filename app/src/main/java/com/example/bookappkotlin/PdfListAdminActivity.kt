package com.example.bookappkotlin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookappkotlin.databinding.ActivityPdfListAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@OptIn(UnstableApi::class)
class PdfListAdminActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityPdfListAdminBinding

    private companion object {
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    //category id, title
    private var categoryId = ""
    private var category = ""

    //array list to hold books
    private lateinit var pdfArrayList: ArrayList<ModelPdf>

    //adapter
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get from intent, that we passed from adapter
        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        //set pdf category
        binding.subTitleTv.text = category

        //themmmmmmmmm
        //init arraylist
        pdfArrayList = ArrayList<ModelPdf>()

        //init adapter with empty list
        adapterPdfAdmin = AdapterPdfAdmin(this, pdfArrayList, pdfArrayList)

        //set layout manager and adapter for RecyclerView
        binding.booksRv.layoutManager = LinearLayoutManager(this)
        binding.booksRv.adapter = adapterPdfAdmin
        //themmmmmmmmm

        //load pdfs

        loadPdfList()

        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //filter data
                try {
                    adapterPdfAdmin.filter!!.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }) // Add closing parenthesis here

    }

//    private fun loadPdfList() {
//        //in it arraylist
//        pdfArrayList = ArrayList<ModelPdf>()
//
//        val ref = FirebaseDatabase.getInstance().getReference("Books")
//        ref.orderByChild("categoryId").equalTo(categoryId)
//            .addValueEventListener(object : ValueEventListener {
//                @OptIn(UnstableApi::class)
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    //clear list before start adding data into it
//                    pdfArrayList.clear()
//                    for (ds in snapshot.children) {
//                        //get data
//                        val model = ds.getValue(ModelPdf::class.java)
//                        //add to list
//                        if (model  != null) {
//                            pdfArrayList.add(model)
//                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
//                        }
//                    }
//                    //setup adpater
//                    adapterPdfAdmin = AdapterPdfAdmin(this@PdfListAdminActivity, pdfArrayList, pdfArrayList)
//                    binding.booksRv.adapter = adapterPdfAdmin
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                }
//            })
//    }
private fun loadPdfList() {
    val ref = FirebaseDatabase.getInstance().getReference("Books")
    ref.orderByChild("category").equalTo(categoryId)
        .addValueEventListener(object : ValueEventListener {
            @OptIn(UnstableApi::class)
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before start adding data into it
                pdfArrayList.clear()
                for (ds in snapshot.children) {
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    if (model  != null) {
                        pdfArrayList.add(model)
                        Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                    }
                }
                //notify adapter that data has changed
                adapterPdfAdmin.notifyDataSetChanged()

                //log for debugging
                Log.d(TAG, "onDataChange: pdfArrayList size: ${pdfArrayList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                //log for debugging
                Log.d(TAG, "onCancelled: Error: ${error.message}")
            }
        })
}
}