package com.example.footprintfoods

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductActivity : AppCompatActivity() {
    // Setup Firebase variables
    private val db = Firebase.firestore
    private lateinit var prodDescription: String
    private lateinit var prodStorage: String
    // Setup Market DB variables
    private lateinit var productTitle: String
    private lateinit var productCarbon: String
    private lateinit var productPrice: String
    private lateinit var productUrl: String
    private lateinit var marketTitle: String
    private lateinit var productCategory: String
    private lateinit var transitionImage: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme & views
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_product)
        supportPostponeEnterTransition()
        // Pull data from Intent
        if (intent.hasExtra("prodTitle")){
            productTitle = intent.getStringExtra("prodTitle")
            productCarbon = intent.getStringExtra("prodCarbon")
            productPrice = intent.getStringExtra("prodPrice")
            productPrice = intent.getStringExtra("prodPrice")
            productUrl = intent.getStringExtra("url")
            marketTitle = intent.getStringExtra("marketTitle")
            productCategory = intent.getStringExtra("productCategory")
            transitionImage = intent.getStringExtra("transitionNameImage")
        }
        // Pull from DB
        databasePull()
        // Setup Variables
        val prodImage = findViewById<ImageView>(R.id.productDetailsImage)
        val prodTitle = findViewById<MaterialToolbar>(R.id.productDetailsTitle)
        val prodDesc = findViewById<TextView>(R.id.productDetailsDescription)
        val prodFarmers = findViewById<RadioGroup>(R.id.productDetailsFarmerRadio)
        val prodStorage = findViewById<TextView>(R.id.productDetailsUsageDescription)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.productDetailsToolbarLayout)
        // Initialise variables
        Glide.with(this).load(productUrl).into(prodImage)
        prodTitle.title = productTitle
        prodImage.transitionName = transitionImage
        setSupportActionBar(prodTitle)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Back button onClickListener
        prodTitle.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }
        if (marketTitle == "Loughborough Farmers' and Craft Market") {
            toolbarLayout.setExpandedTitleColor(getColor(R.color.white))
            toolbarLayout.setCollapsedTitleTextColor(getColor(R.color.white))
            prodTitle.navigationIcon?.setTint(getColor(R.color.white))
        } else {
            toolbarLayout.setExpandedTitleColor(getColor(R.color.black))
            prodTitle.navigationIcon?.setTint(getColor(R.color.black))
        }
        Handler().postDelayed({
            prodDesc.text = prodDescription
            prodStorage.text = this.prodStorage
            supportStartPostponedEnterTransition()}, 200)
    }
    // Database pull function
    private fun databasePull() {
        Log.d("Product Activity", "Starting pulling Produce from DB")
        db.collection("Foodmarkets")
                .document(marketTitle)
                .collection(productCategory)
                .document(productTitle)
                .get()
                .addOnSuccessListener { result ->
                    prodDescription = result?.get("Description").toString()
                    prodStorage = result?.get("Storage").toString()
                }
    }
}