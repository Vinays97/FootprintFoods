package com.example.footprintfoods

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MarketActivity : AppCompatActivity() {
    // Initialise Firebase variables
    private val db = Firebase.firestore
    // Initialise DB variables
    private lateinit var marketTitle: String
    private var streetName: String? = null
    private var townName: String? = null
    private var postCode: String? = null
    private var doneDB: Boolean = false
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme & views
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_market)
        // Pull data from Intent
        if (intent.hasExtra("itemTitle")){
            marketTitle = intent.getStringExtra("itemTitle")
        }
        // Pull from DB
        databasePull()
        // Initialise Variables
        val marketTitleText = findViewById<TextView>(R.id.marketToolbarTitle)
        val marketSubtitleText = findViewById<TextView>(R.id.marketToolbarSubtitle)
        val marketMapButton = findViewById<Button>(R.id.marketToolbarButton)
        // Set values for variables
        marketTitleText.text = marketTitle
        Handler().postDelayed({
            val concatText = "$streetName • $townName • $postCode"
            Log.d(ContentValues.TAG, concatText)
            marketMapButton.text = concatText
                              }, 200)
    }
    // Database pull function
    private fun databasePull() {
        db.collection("Foodmarkets")
            .document(marketTitle)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    Log.d(ContentValues.TAG, "Starting pulling from DB")
                    streetName = result.get("Address.Street").toString()
                    townName = result.get("Address.Town").toString()
                    postCode = result.get("Address.Postcode").toString()
                    doneDB = true
                    Log.d(ContentValues.TAG, "Done from DB")
                } else {
                    Log.d(ContentValues.TAG, "No results")
                }
            }
    }
}