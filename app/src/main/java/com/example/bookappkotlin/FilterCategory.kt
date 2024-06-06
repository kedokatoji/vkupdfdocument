package com.example.bookappkotlin

import android.widget.Filter

class FilterCategory : Filter {
    //arraylist in which me want to search
    private var filterList: ArrayList<ModelCategory>

    //adapter in which filter need to be implemented
    private var adapter: AdapterCategory

    //constructor
    constructor(filterList: ArrayList<ModelCategory>, adapter: AdapterCategory) : super() {
        this.filterList = filterList
        this.adapter = adapter
    }

    //    override fun performFiltering(constraint: CharSequence?): FilterResults {
//        val constraint = constraint
//        val results = FilterResults()
//
//        //value should not be null and not empty
//        if (constraint != null && constraint.isNotEmpty()) {
//           //searched value is nor null empty
//            //change to upper case, or lower case to avoid case sensitivity
//            constraint = constraint.toString().uppercase()
//            val filteredModels: ArrayList<ModelCategory> = ArrayList()
//            for (i in 0 until filterList.size) {
//                //validate
//                if (filterList[i].category.uppercase().contains(constraint)) {
//                    //add to filtered list
//                    filteredModels.add(filterList[i])
//                }
//            }
//            results.count = filteredModels.size
//            results.values = filteredModels
//
//        }
//        else {
//            //value is null, return all original values
//            results.count = filterList.size
//            results.values = filterList
//        }
//
//        return results
//    }
//
//    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//        //apply filter chang
//        adapter.categoryArrayList = results.values as ArrayList<ModelCategory>
//
//        //notify changes
//        adapter.notifyDataSetChanged()
//    }
//
//
//}
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()

        //value should not be null and not empty
        if (constraint != null && constraint.isNotEmpty()) {
            //searched value is nor null empty
            //change to upper case, or lower case to avoid case sensitivity
            val constraintUpper = constraint.toString().uppercase()
            val filteredModels: ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterList.size) {
                //validate
                if (filterList[i].category.uppercase().contains(constraintUpper)) {
                    //add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels

        } else {
            //value is null, return all original values
            results.count = filterList.size
            results.values = filterList
        }

        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        //apply filter chang
        if (results != null) {
            adapter.categoryArrayList = results.values as ArrayList<ModelCategory>

            //notify changes
            adapter.notifyDataSetChanged()
        }
    }
}