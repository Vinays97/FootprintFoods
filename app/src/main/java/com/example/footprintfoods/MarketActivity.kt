package com.example.footprintfoods

import android.content.ContentValues
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlin.collections.ArrayList

class MarketActivity : AppCompatActivity() {
    // Setup Firebase variables
    private val db = Firebase.firestore
    // Setup Market DB variables
    lateinit var marketTitle: String
    private lateinit var marketDate: String
    private lateinit var transitionImage: String
    private lateinit var transitionTitle: String
    private var streetName: String? = null
    private var townName: String? = null
    private var postCode: String? = null
    private var imageURL: String? = null
    private var marketDetails: String? = null
    private var marketProduceTitles = arrayListOf<String>()
    // Setup Produce DB variables
    var marketProduceFruit = arrayListOf<String>()
    var marketProduceMeat = arrayListOf<String>()
    var marketProduceVegetable = arrayListOf<String>()
    var marketProduceDairy = arrayListOf<String>()
    // Setup other variables
    private var arrayTabs = arrayListOf<String>()
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme & views
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_market)
        supportPostponeEnterTransition()
        // Pull data from Intent
        if (intent.hasExtra("itemTitle")){
            marketTitle = intent.getStringExtra("itemTitle")
            marketDate = intent.getStringExtra("itemDate")
            transitionImage = intent.getStringExtra("transitionNameImage")
            transitionTitle = intent.getStringExtra("transitionNameTitle")
        }
        // Pull from DB
        databasePull()
        // Initialise Variables
        val marketImage = findViewById<ImageView>(R.id.marketToolbarImage)
        val marketTitleText = findViewById<EditText>(R.id.marketToolbarTitle)
        val marketDateButton = findViewById<Button>(R.id.marketToolbarButtonDate)
        val marketMapButton = findViewById<Button>(R.id.marketToolbarButtonLocation)
        val backButton = findViewById<ImageView>(R.id.marketBackButton)
        val marketDescription = findViewById<TextView>(R.id.marketToolbarDescription)
        // Set animation
        val marketActivityView = findViewById<View>(R.id.marketActivityView)
        marketActivityView.transitionName = transitionTitle
        marketImage.transitionName = transitionImage
        // onClickListener for back button
        backButton.setOnClickListener{
            supportFinishAfterTransition()
        }
        // Set values for variables
        marketTitleText.setText(marketTitle)
        marketDateButton.text = marketDate
        marketTitleText.movementMethod = ScrollingMovementMethod()
        Handler().postDelayed({
            databaseProductPull()
            val concatText = "$streetName • $postCode"
            Log.d(ContentValues.TAG, concatText)
            marketMapButton.text = concatText
            marketDescription.text = marketDetails
            Glide.with(this).load(imageURL).into(marketImage)
            Handler().postDelayed({
                setupTabs()
                supportStartPostponedEnterTransition()}, 200)}, 200)
    }
    // Database pull function
    private fun databasePull() {
        Log.d(ContentValues.TAG, "Starting pulling Markets from DB")
        db.collection("Foodmarkets")
            .document(marketTitle)
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    streetName = result.get("Address.Street").toString()
                    townName = result.get("Address.Town").toString()
                    postCode = result.get("Address.Postcode").toString()
                    imageURL = result.get("photoURL").toString()
                    marketDetails = result.get("MarketDetails").toString()
                    marketProduceTitles = result.get("Produce") as ArrayList<String>
                    Log.d(ContentValues.TAG, "Produce Titles = $marketProduceTitles")
                } else {
                    Log.d(ContentValues.TAG, "No results")
                }
            }
        Log.d(ContentValues.TAG, "Produce Titles = $marketProduceTitles")
        Log.d(ContentValues.TAG, "Markets done from DB")
    }
    // Setup tabs function
    private fun setupTabs() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        for (i in marketProduceTitles) {
            when (i) {
                "Fruit" -> {
                    adapter.addFragment(FruitFragment(), "Fruit")
                    arrayTabs.add("Fruit")
                }
                "Vegetables" -> {
                    adapter.addFragment(VegetableFragment(), "Vegetables")
                    arrayTabs.add("Vegetables")
                }
                "Dairy" -> {
                    adapter.addFragment(DairyFragment(), "Dairy")
                    arrayTabs.add("Dairy")
                }
                "Meat Fish Poultry" -> {
                    adapter.addFragment(MeatFragment(), "Meat, Fish & Poultry")
                    arrayTabs.add("Meat")
                }
            }
        }
        val viewPager = findViewById<ViewPager>(R.id.marketViewPager)
        val tabs = findViewById<TabLayout>(R.id.marketTabs)
        viewPager.adapter = adapter
        tabs.setupWithViewPager(viewPager)
        var i = 0
        for (j in arrayTabs){
            when (j) {
                "Fruit" -> {
                    tabs.getTabAt(i)!!.setIcon(R.drawable.ic_fruit_icon)
                }
                "Vegetables" -> {
                    tabs.getTabAt(i)!!.setIcon(R.drawable.ic_vegetables_icon)
                }
                "Dairy" -> {
                    tabs.getTabAt(i)!!.setIcon(R.drawable.ic_dairy_icon)
                }
                "Meat" -> {
                    tabs.getTabAt(i)!!.setIcon(R.drawable.ic_meat_icon)
                }
            }
            i += 1
        }
    }
    // Database pull function
    private fun databaseProductPull() {
        Log.d(ContentValues.TAG, "Starting pulling Produce from DB")
        for (i in marketProduceTitles.indices){
            db.collection("Foodmarkets")
                    .document(marketTitle)
                    .collection(marketProduceTitles[i])
                    .get()
                    .addOnSuccessListener { result ->
                        if (result != null) {
                            for (document in result){
                                when (marketProduceTitles[i]) {
                                    "Fruit" -> {
                                        val concat = "${document.id}/" +
                                                "${document.get("Carbon").toString()}/" +
                                                "${document.get("Price").toString()}/" +
                                                "${document.get("photoURL").toString()}/"
                                        marketProduceFruit.add(concat)
                                    }
                                    "Meat Fish Poultry" -> {
                                        val concat = "${document.id}/" +
                                                "${document.get("Carbon").toString()}/" +
                                                "${document.get("Price").toString()}/" +
                                                "${document.get("photoURL").toString()}/"
                                        marketProduceMeat.add(concat)
                                    }
                                    "Vegetables" -> {
                                        val concat = "${document.id}/" +
                                                "${document.get("Carbon").toString()}/" +
                                                "${document.get("Price").toString()}/" +
                                                "${document.get("photoURL").toString()}/"
                                        marketProduceVegetable.add(concat)
                                    }
                                    "Dairy" -> {
                                        val concat = "${document.id}/" +
                                                "${document.get("Carbon").toString()}/" +
                                                "${document.get("Price").toString()}/" +
                                                "${document.get("photoURL").toString()}/"
                                        marketProduceDairy.add(concat)
                                    }
                                }
                            }
                            Log.d(ContentValues.TAG, "Fruit = $marketProduceFruit")
                            Log.d(ContentValues.TAG, "Meat = $marketProduceMeat")
                            Log.d(ContentValues.TAG, "Vegetables = $marketProduceVegetable")
                            Log.d(ContentValues.TAG, "Dairy = $marketProduceDairy")
                        } else {
                            Log.d(ContentValues.TAG, "No results")
                        }
                    }
        }
        Log.d(ContentValues.TAG, "Produce done from DB")
    }
}

