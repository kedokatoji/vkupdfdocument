package com.example.bookappkotlin.models

class ModelCategory {

    //variables, must mactch in firebase
    var id: String = ""
    var category: String = ""
    var timestamp: Long = 0
    var uid: String = ""

    //empty constructor, required by firebaes
    constructor() {
    }
    //parameterized constructor
    constructor(id: String, category: String, timestamp: Long, uid: String) {
        this.id = id
        this.category = category
        this.timestamp = timestamp
        this.uid = uid
    }

}