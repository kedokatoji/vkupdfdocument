//package com.example.bookappkotlin
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.bookappkotlin.databinding.RowPdfAdminBinding
//
////class AdapterPdfAdmin :RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>(){
////
////    //context
////    private var context: Context
////
////    //array list to hold pdfs
////    private var pdfArrayList: ArrayList<ModelPdf>
////
////
////
////
////
////    //viewBinding
////    private lateinit var binding: RowPdfAdminBinding
////
////
////    //constructor
////    constructor(context: Any, pdfArrayList: ArrayList<ModelPdf>) : super() {
////        this.context = context
////        this.pdfArrayList = pdfArrayList
////    }
////
////    //view holder class for row_pdf_admin.xml
////
////
////    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
////        //bind/inflate layout row_pdf_admin.xml
////        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)
////    }
////
////    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
////        TODO("Not yet implemented")
////    }
////
////    override fun getItemCount(): Int {
////        return pdfArrayList.size //items counts
////    }
////
////    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView){
////        //UI views of row_pdf_admin.xml
////        val pdfView = binding.pdfView
////        val progressBar = binding.progressBar
////        val titleTv = binding.titleTv
////        val descriptionTv = binding.descriptionTv
////        val categoryTv = binding.categoryTv
////        val sizeTv = binding.sizeTv
////        val dateTv = binding.dateTv
////        val moreBtn = binding.moreBtn
////    }
////
////}
//class AdapterPdfAdmin(private var context: Context, var pdfArrayList: ArrayList<ModelPdf>) : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>() {
//
//    // viewBinding
//    private lateinit var binding: RowPdfAdminBinding
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
//        // bind/inflate layout row_pdf_admin.xml
//        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)
//        return HolderPdfAdmin(binding.root)
//    }
//
//    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
////        get data, set data, handle click
//        //getdata
//        val model = pdfArrayList[position]
//        val pdfId = model.id
//        val categoryId = model.categoryId
//        val title = model.title
//        val description = model.description
//        val pdfUrl = model.url
//        val timestamp = model.timestamp
//
//        //convert timestamp to dd/mm/yyyy
//        val formattedDate = MyApplication.formatTimeStamp(timestamp)
//
//        //set data
//        holder.titleTv.text = title
//        holder.descriptionTv.text = description
//        holder.dateTv.text = formattedDate
//
//        //load further detail like category, pdf from url, pdf size
//        //category id
//        MyApplication.loadCategory(categoryId, holder.categoryTv)
//
//        //we don't need page number here, pas null for page number
//        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)
//
//
//        //load pdf size
//        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)
//        //lets create an application class that will contain the functions that will be used mltiple places in app
//
//    }
//
//    override fun getItemCount(): Int {
//        return pdfArrayList.size // items counts
//    }
//
//    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        // UI views of row_pdf_admin.xml
//        val pdfView = binding.pdfView
//        val progressBar = binding.progressBar
//        val titleTv = binding.titleTv
//        val descriptionTv = binding.descriptionTv
//        val categoryTv = binding.categoryTv
//        val sizeTv = binding.sizeTv
//        val dateTv = binding.dateTv
//        val moreBtn = binding.moreBtn
//    }
//}
package com.example.bookappkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.bookappkotlin.databinding.RowPdfAdminBinding

class AdapterPdfAdmin(private var context: Context, var pdfArrayList: ArrayList<ModelPdf>) : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>(), Filterable {

    // viewBinding
    private lateinit var binding: RowPdfAdminBinding

    // filter list and filter
    private var filterList: ArrayList<ModelPdf>
    private var filter: FilterPdfAdmin? = null

    init {
        this.filterList = pdfArrayList
        this.filter = FilterPdfAdmin(filterList, this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        // bind/inflate layout row_pdf_admin.xml
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        // get data, set data, handle click
        // get data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        // convert timestamp to dd/mm/yyyy
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        // set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate

        // load further detail like category, pdf from url, pdf size
        // category id
        MyApplication.loadCategory(categoryId, holder.categoryTv)

        // we don't need page number here, pass null for page number
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView, holder.progressBar, null)

        // load pdf size
        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)


        //handle click, show dialog with option 1) editbook, 2) delete book
        holder.moreBtn.setOnClickListener {
            moreOptionDialog(model, holder)
        }
    }

    private fun moreOptionDialog(model: ModelPdf, holder: AdapterPdfAdmin.HolderPdfAdmin) {
        //get id, url, title of book
        val bookId = model.id
        val bookUrl = model.url
        val bookTitle = model.title

        //option to show in dialog
        val options = arrayOf("Edit", "Delete")

        //alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options) {dialog, position ->
                //handle item click
                if (position==0){
                    //edit is clicked
                    val intent = Intent(context, PdfEditActivity::class.java)
                    intent.putExtra("bookId", bookId) //passed bookId , will be used to edit the book
                    context.startActivity(intent)
                }
                else if (position ==1){
                    //delete is clicked
                    MyApplication.deleteBook(context, bookId, bookUrl, bookTitle)
                }
            }
            .show()
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size // items counts
    }





    override fun getFilter(): Filter {
        if (filter ==null){
            filter = FilterPdfAdmin(filterList, this)

        }
        return filter as FilterPdfAdmin
    }

    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // UI views of row_pdf_admin.xml
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }
}