//package com.example.bookappkotlin
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.example.bookappkotlin.databinding.ActivityRegisterBinding
//import com.google.firebase.auth.FirebaseAuth
//
//class RegisterActivity : AppCompatActivity() {
//
//    //view binding
//    private lateinit var binding: ActivityRegisterBinding
//
//    //firebase auth
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    //progress dialog
//    private lateinit var progressDialog: ProgressDialog
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRegisterBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        //init firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()
//
//        //init progress dialog, will show while creating account / Register user
//        progressDialog = ProgressDialog(this)
//        progressDialog.setTitle("Please wait")
//        progressDialog.setCanceledOnTouchOutside(false)
//
//        //handle back button click
//        binding.backBtn.setOnClickListener {
//            onBackPressed() //go to previous screen
//        }
//
//        //handle click, begin registration
//
//    }
//}
//package com.example.bookappkotlin
//
//import android.os.Bundle
//import android.util.Patterns
//import android.view.View
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.bookappkotlin.databinding.ActivityRegisterBinding
//import com.google.firebase.auth.AuthResult
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//
//class RegisterActivity : AppCompatActivity() {
//
//    //view binding
//    private lateinit var binding: ActivityRegisterBinding
//
//    //firebase auth
//    private lateinit var firebaseAuth: FirebaseAuth
//
//    //progress bar
//    private lateinit var progressBar: ProgressBar
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityRegisterBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        //init firebase auth
//        firebaseAuth = FirebaseAuth.getInstance()
//
//        //init progress bar, will show while creating account / Register user
//        progressBar = ProgressBar(this)
//        progressBar.visibility = View.GONE
//
//        //handle back button click
//        binding.backBtn.setOnClickListener {
//            onBackPressed() //go to previous screen
//        }
//
//        //handle click, begin registration
//        binding.registerBtn.setOnClickListener {
//            validateData()
//        }
//
//    }
//
//    private var name = ""
//    private var email = ""
//    private var password = ""
//
//    private fun validateData() {
//        //input data
//        name = binding.nameEt.text.toString().trim()
//        email = binding.emailEt.text.toString().trim()
//        password = binding.passwordEt.text.toString().trim()
//        val cPassword = binding.cPasswordEt.text.toString().trim()
//
//        //2 validate data
//        if (name.isEmpty()) {
//            //empty name...
//            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
//        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//            //invalid email pattern
//            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show()
//        } else if (password.isEmpty()) {
//            //empty password
//            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show()
//        } else if (cPassword.isEmpty()) {
//            //empty confirm password
//            Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show()
//        } else if (password != cPassword) {
//            //password and confirm password not same
//            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show()
//        } else {
//            createUserAccount()
//        }
//    }
//
//    private fun createUserAccount() {
//        //3) create account - firebase auth
//        progressDialog.setMessage("Creating account...")
//        progressDialog.show()
//
//        //create user in firebase auth
//        firebaseAuth.createUserWithEmailAndPassword(email, password)
//            .addOnSuccessListener {
//                //sign up success
//                updateUserInfo()
//            }
//            .addOnFailureListener { e ->
//                //sign up failed
//                progressDialog.dismiss()
//                Toast.makeText(
//                    this,
//                    "Failed creating account due to ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//    }
//
//    private fun updateUserInfo(): AuthResult? {
//        //4) save user info - firebase realtime database
//        progressDialog.setMessage("Saving user info...")
//
//        //timestamp
//        val timestamp = System.currentTimeMillis()
//
//        //get current user uid, since user is registered so we can get it now
//        val uid = firebaseAuth.uid
//
//        //setup data to add in database
//        val hashMap: HashMap<String, Any> = HashMap()
//        hashMap["uid"] = uid
//        hashMap["email"] = email
//        hashMap["name"] = name
//        hashMap["profileImage"] = "" //add empty profile image
//        hashMap["userType"] = "user"
//        hashMap["timestamp"] = timestamp
//
//        //set data to db
//        val ref = FirebaseDatabase.getInstance().getReference("Users")
//        ref.child(uid!!)
//            .setValue(hashMap)
//            .addOnSuccessListener {
//                //user info saved, open user dashboard
//                progressDialog.dismiss()
//                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
//                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
//                finish()
//            }
//
//            .addOnFailureListener() { e ->
//                //failed adding data to db
//                progressDialog.dismiss()
//                Toast.makeText(
//                    this,
//                    "Failed saving user info due to ${e.message}",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//
//
//    }
//}

package com.example.bookappkotlin.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookappkotlin.databinding.ActivityRegisterBinding
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    //view binding
    private lateinit var binding: ActivityRegisterBinding

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //progress bar
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //init progress bar, will show while creating account / Register user
        progressBar = ProgressBar(this)
        progressBar.visibility = View.GONE

        //handle back button click
        binding.backBtn.setOnClickListener {
            onBackPressed() //go to previous screen
        }

        //handle click, begin registration
        binding.registerBtn.setOnClickListener {
            validateData()
        }

    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        //input data
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        val cPassword = binding.cPasswordEt.text.toString().trim()

        //2 validate data
        if (name.isEmpty()) {
            //empty name...
            Toast.makeText(this, "Enter your name...", Toast.LENGTH_SHORT).show()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //invalid email pattern
            Toast.makeText(this, "Invalid email pattern...", Toast.LENGTH_SHORT).show()
        } else if (password.isEmpty()) {
            //empty password
            Toast.makeText(this, "Enter password...", Toast.LENGTH_SHORT).show()
        } else if (cPassword.isEmpty()) {
            //empty confirm password
            Toast.makeText(this, "Confirm password...", Toast.LENGTH_SHORT).show()
        } else if (password != cPassword) {
            //password and confirm password not same
            Toast.makeText(this, "Password doesn't match...", Toast.LENGTH_SHORT).show()
        } else {
            createUserAccount()
        }
    }

    private fun createUserAccount() {
        //3) create account - firebase auth
        progressBar.visibility = View.VISIBLE

        //create user in firebase auth
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                //sign up success
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                //sign up failed
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Failed creating account due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun updateUserInfo(): AuthResult? {
        //4) save user info - firebase realtime database
        progressBar.visibility = View.VISIBLE

        //timestamp
        val timestamp = System.currentTimeMillis()

        //get current user uid, since user is registered so we can get it now
        val uid = firebaseAuth.uid

        //setup data to add in database
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = uid ?: return null
        hashMap["email"] = email
        hashMap["name"] = name
        hashMap["profileImage"] = "" //add empty profile image
        hashMap["userType"] = "user"
        hashMap["timestamp"] = timestamp

        //set data to db
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(uid)
            .setValue(hashMap)
            .addOnSuccessListener {
                //user info saved, open user dashboard
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Account created...", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, DashboardUserActivity::class.java))
                finish()
            }

            .addOnFailureListener() { e ->
                //failed adding data to db
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Failed saving user info due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        return null
    }
}
