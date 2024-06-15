package com.example.bookappkotlin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookappkotlin.adapters.AdapterPdfUser
import com.example.bookappkotlin.databinding.FragmentBooksUserBinding
import com.example.bookappkotlin.models.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class BooksUserFragment : Fragment {

    //view binding fragment_books_user.xml => FragmentBooksUserBinding
    private lateinit var binding: FragmentBooksUserBinding

    public companion object{
        private const val TAG = "BOOKS_USER_TAG"

        //receive data from activity to load book e.g, category id, category, uid
        public fun newInstance(categoryId: String, category: String, uid:String): BooksUserFragment{
            val fragment = BooksUserFragment()
            //put data to bundle intent
            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("category", category)
            args.putString("uid", uid)
            fragment.arguments = args
            return fragment
        }
    }

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    constructor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get arguments that we passed in newInstance method
        val args = arguments
        if (args != null){
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBooksUserBinding.inflate(LayoutInflater.from(context), container, false)

        //load pdf according to category, this fragment will have new instance to load each category pdfs
        Log.d(TAG, "onCreateView: Category: $category")
        if (category == "All"){
            //load all pdf
            loadAllBooks()
        }
        else if (category == "Most Viewed"){
            //load most viewed pdf
            loadMostViewedDownloadedBooks("viewsCount")
        }
        else if (category == "Most Downloaded"){
            //load most downloaded book
            loadMostViewedDownloadedBooks("downloadsCount")
        }
        else{
            //load selected category books
            loadCategorizedBooks()
        }

        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before text changes
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Filter data as text changes
                try {
                    adapterPdfUser.filter.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: SEARCH EXCEPTION: ${e.message}")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // Do something after text changes
            }
        })


        return binding.root
    }

    private fun loadAllBooks() {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data into it
                pdfArrayList.clear()
                for (ds  in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    //add to list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                //set adapter to recyclerview
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

//    private fun loadMostViewedDownloadedBooks(orderBy: String) {
//        //init list
//        pdfArrayList = ArrayList()
//        val ref = FirebaseDatabase.getInstance().getReference("Books")
//        ref.orderByChild(orderBy).limitToLast(10) //load most viewed or most downloaded
//            .addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                //clear list before adding data into it
//                pdfArrayList.clear()
//                for (ds  in snapshot.children){
//                    //get data
//                    val model = ds.getValue(ModelPdf::class.java)
//                    //add to list
//                    pdfArrayList.add(model!!)
//                }
//                //setup adapter
//                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
//                //set adapter to recyclerview
//                binding.booksRv.adapter = adapterPdfUser
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
//    }
private fun loadMostViewedDownloadedBooks(orderBy: String) {
    //init list
    pdfArrayList = ArrayList()
    val ref = FirebaseDatabase.getInstance().getReference("Books")
    ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            //clear list before adding data into it
            pdfArrayList.clear()
            for (ds  in snapshot.children){
                //get data
                val model = ds.getValue(ModelPdf::class.java)
                //add to list
                pdfArrayList.add(model!!)
            }
            //sort and limit the list
            if (orderBy == "viewsCount") {
                pdfArrayList.sortByDescending { it.viewsCount }
            } else if (orderBy == "downloadsCount") {
                pdfArrayList.sortByDescending { it.downloadsCount }
            }
            if (pdfArrayList.size > 10) {
                pdfArrayList = ArrayList(pdfArrayList.take(10))
            }
            //setup adapter
            adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
            //set adapter to recyclerview
            binding.booksRv.adapter = adapterPdfUser
        }

        override fun onCancelled(error: DatabaseError) {

        }
    })
}

    private fun loadCategorizedBooks() {
        //init list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before adding data into it
                    pdfArrayList.clear()
                    for (ds  in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelPdf::class.java)
                        //add to list
                        pdfArrayList.add(model!!)
                    }
                    //setup adapter
                    adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                    //set adapter to recyclerview
                    binding.booksRv.adapter = adapterPdfUser
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }




}