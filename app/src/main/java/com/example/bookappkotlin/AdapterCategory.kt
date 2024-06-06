package com.example.bookappkotlin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bookappkotlin.databinding.RowCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory : RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {
    private lateinit var filterList: java.util.ArrayList<ModelCategory>
    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var binding: RowCategoryBinding
    private var filter: FilterCategory? = null
    //constructor
    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>) {
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }



    //ViewHolder class to hold/init UI views for row_caterogy.xml
    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) {
        //init ui views
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn:ImageButton = binding.deleteBtn

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        //inflate bind row_category.xml
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderCategory(binding.root)
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
//
        //get data
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //set data
        holder.categoryTv.text = category

        //handle click, delete category
        holder.deleteBtn.setOnClickListener {
            //confirm before delete
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Confirm") { a, d ->
                    //delete
                    Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()
                    //delete category
                    deleteCategory(model, holder)
                }
                .setNegativeButton("CANCEL") { a, d ->
                    //cancel, dismiss dialog
                    a.dismiss()
                }
                .show()
        }

        //handle click, start pdf list admin activity, also pass pdf id, title
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("category", category)
            intent.putExtra("categoryId", id)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model: ModelCategory, holder: HolderCategory) {
// get id of category to delete
        val id = model.id
        //firebase db > categories > categoryID
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                //deleted
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                //failed
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterCategory(filterList, this)
        }
        return filter as FilterCategory
    }


}