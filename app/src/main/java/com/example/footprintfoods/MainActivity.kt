package com.example.footprintfoods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    // Setup Firebase variables
    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_main)
        // Initialise variables
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        //Checks if user is already authenticated.
        if(user != null){
            Log.d("DBCheck", "Account UID: " + user.uid)
            val data = hashMapOf("Name" to user.displayName)
            db.collection("User")
                    .document(user.uid)
                    .set(data)
            val homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        } else {
            val signInIntent = Intent(this, SignInActivity::class.java)
            startActivity(signInIntent)
            finish()
        }
    }
}