package com.example.footprintfoods

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.collections.ArrayList

class MarketActivity : AppCompatActivity() {
    // Initialise Firebase variables
    private val db = Firebase.firestore
    val storageReference = Firebase.storage.reference
    // Initialise DB variables
    private lateinit var marketTitle: String
    private var streetName: String? = null
    private var townName: String? = null
    private var postCode: String? = null
    private var imageURL: String? = null
    private var marketProduce = arrayListOf<String>()
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
        val marketImage = findViewById<ImageView>(R.id.marketToolbarImage)
        val marketTitleText = findViewById<TextView>(R.id.marketToolbarTitle)
        val marketSubtitleText = findViewById<TextView>(R.id.marketToolbarSubtitle)
        val marketMapButton = findViewById<Button>(R.id.marketToolbarButton)
        // Set values for variables
        marketTitleText.text = marketTitle
        Handler().postDelayed({
            setupTabs()
            val concatText = "$streetName • $townName • $postCode"
            Log.d(ContentValues.TAG, concatText)
            Log.d(ContentValues.TAG, "$marketProduce")
            marketMapButton.text = concatText
            Glide.with(this).load(imageURL).into(marketImage)
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
                    imageURL = result.get("photoURL").toString()
                    marketProduce = result.get("Produce") as ArrayList<String>
                    doneDB = true
                    Log.d(ContentValues.TAG, "Done from DB")
                } else {
                    Log.d(ContentValues.TAG, "No results")
                }
            }
    }
    // Setup tabs function
    private fun setupTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        for (i in marketProduce) {
            when (i) {
                "Fruit" -> {
                    adapter.addFragment(FruitFragment(), "Fruit")
                }
                "Vegetables" -> {
                    adapter.addFragment(VegetableFragment(), "Vegetables")
                }
                "Dairy" -> {
                    adapter.addFragment(DairyFragment(), "Dairy")
                }
                "Meat Fish Poultry" -> {
                    adapter.addFragment(MeatFragment(), "Meat, Fish & Poultry")
                }
            }
        }
        val viewPager = findViewById<ViewPager>(R.id.marketViewPager)
        val tabs = findViewById<TabLayout>(R.id.marketTabs)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
    }
}