package com.example.footprintfoods

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.get
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class ProductActivity : AppCompatActivity() {
    // Setup Firebase variables
    private val db = Firebase.firestore
    private lateinit var prodDescription: String
    private lateinit var prodStorage: String
    var produceFarmers = arrayListOf<String>()
    var farmerPrices = arrayListOf<String>()
    var farmerCarbonPrice = arrayListOf<String>()
    // Setup Market DB variables
    private lateinit var productTitle: String
    private lateinit var productCarbon: String
    private lateinit var productPrice: String
    private lateinit var productUrl: String
    private lateinit var marketTitle: String
    private lateinit var productCategory: String
    private lateinit var transitionImage: String
    // Setup RadioGroup
    lateinit var farmerRadioGroup: RadioGroup
    // Other variables
    lateinit var prodQuantity: TextView
    lateinit var totalPrice: TextView
    lateinit var carbonPriceSelected: String

    // onCreate function
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
        databasePulFarmer()
        // Setup Variables
        val prodImage = findViewById<ImageView>(R.id.productDetailsImage)
        val prodTitle = findViewById<MaterialToolbar>(R.id.productDetailsTitle)
        val prodDesc = findViewById<TextView>(R.id.productDetailsDescription)
        val prodStorage = findViewById<TextView>(R.id.productDetailsUsageDescription)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.productDetailsToolbarLayout)
        val minusButton = findViewById<Button>(R.id.productDetailsMinus)
        val plusButton = findViewById<Button>(R.id.productDetailsPlus)
        val addToCart = findViewById<MaterialButton>(R.id.productDetailsAddToCart)
        prodQuantity = findViewById(R.id.productDetailsQuantityText)
        totalPrice = findViewById(R.id.productDetailsTotalPrice)
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
        minusButton.setOnClickListener{
            var quantity: Int = prodQuantity.text.toString().toInt()
            if (quantity > 1){
                quantity -= 1
            }
            prodQuantity.text = quantity.toString()
            setPrice()
        }
        plusButton.setOnClickListener{
            var quantity: Int = prodQuantity.text.toString().toInt()
            quantity += 1
            prodQuantity.text = quantity.toString()
            setPrice()
        }
        if (marketTitle == "Loughborough Farmers' and Craft Market") {
            toolbarLayout.setExpandedTitleColor(getColor(R.color.white))
            toolbarLayout.setCollapsedTitleTextColor(getColor(R.color.white))
            prodTitle.navigationIcon?.setTint(getColor(R.color.white))
        } else if (marketTitle == "Notting Hill Farmers' Market"){
            toolbarLayout.setExpandedTitleColor(getColor(R.color.white))
            toolbarLayout.setCollapsedTitleTextColor(getColor(R.color.white))
            prodTitle.navigationIcon?.setTint(getColor(R.color.white))
        } else {
            toolbarLayout.setExpandedTitleColor(getColor(R.color.black))
            prodTitle.navigationIcon?.setTint(getColor(R.color.black))
        }
        Handler().postDelayed({
            createFarmerRadio()
            prodDesc.text = prodDescription
            prodStorage.text = this.prodStorage
            farmerRadioGroup.setOnCheckedChangeListener{ group, checkId ->
                setPrice()
                carbonPriceSelected = farmerCarbonPrice[checkId]
            }
            farmerRadioGroup.check(0)
            supportStartPostponedEnterTransition()}, 200)
        addToCart.setOnClickListener{
            val product = productTitle
            val price = totalPrice.text.substring(1)
            val quantity = prodQuantity.text
            val carbon = carbonPriceSelected.toString()
            val concat = "$price/$product/$quantity/$carbon"
            val intent = intent
            intent.putExtra("cartInfo", concat)
            setResult(Activity.RESULT_OK, intent)
            supportFinishAfterTransition()
        }
    }
    // Set price function
    private fun setPrice() {
        val farmer = farmerRadioGroup.checkedRadioButtonId
        val price: Double = farmerPrices[farmer].toDouble()
        val quantity: Double = prodQuantity.text.toString().toDouble()
        val total: Double = (price * quantity)
        val totalFormat = String.format("%.2f", total)
        val concat = "??$totalFormat"
        totalPrice.text = concat
    }

    // Create radio buttons
    private fun createFarmerRadio() {
        var i = 0
        farmerRadioGroup = findViewById(R.id.productDetailsFarmerRadio)
        for (farmer in produceFarmers){
            val name = "\u0009" + farmer.substring(0, farmer.indexOf("/"))
            val rem = farmer.substring(farmer.indexOf("/") + 1, farmer.length)
            val price = "??" + rem.substring(0, rem.indexOf("/")) + "/kg"
            val carbon = rem.substring(rem.indexOf("/") + 1, rem.length) + " CO???e"
            val concat = String.format("%s \u0009 \u0009 \u0009 " +
                    "\u0009 \u0009 \u0009 " +
                    "\u0009 \u0009 \u0009 " +
                    "\u0009 \u0009 %s", name, price)
            val concat2 = String.format("%s \u0009 \u0009 \u0009 %s", concat, carbon)
            Log.d("Product Activity", concat)
            val radioButton = RadioButton(this)
            radioButton.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            radioButton.text = concat2
            radioButton.id = i
            farmerRadioGroup.addView(radioButton)
            i += 1
        }
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
        Log.d("Product Activity", "Done from DB")
    }
    // Database farmer pull function
    private fun databasePulFarmer() {
        Log.d("Product Activity", "Starting pulling Produce from DB")
        db.collection("Foodmarkets")
                .document(marketTitle)
                .collection(productCategory)
                .document(productTitle)
                .collection("Seller")
                .orderBy("Carbon", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    if (result != null) {
                        for (document in result) {
                            val farmer = document.id
                            val farmerCarbon = document.get("Carbon").toString()
                            val farmerPrice = document.get("Price").toString()
                            val concat = "$farmer/${farmerPrice}/${farmerCarbon}"
                            Log.d("Product Activity", concat)
                            produceFarmers.add(concat)
                            farmerPrices.add(farmerPrice)
                            farmerCarbonPrice.add(farmerCarbon)
                        }
                    } else {
                        Log.d("Product Activity", "No results found")
                    }
                }
        Log.d("Product Activity", "Done from DB")
    }
}
