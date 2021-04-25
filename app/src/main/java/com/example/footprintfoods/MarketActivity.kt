package com.example.footprintfoods

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.collections.ArrayList

class MarketActivity : AppCompatActivity() {
    // Setup Firebase variables
    private val db = Firebase.firestore
    private lateinit var mAuth: FirebaseAuth
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
    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetConstraint: ConstraintLayout
    var cartData = arrayListOf<String>()
    lateinit var cartTotal: TextView
    lateinit var cartCarbonTotal: TextView
    // Setup fragment variables
    lateinit var cartFragment: CartFragment
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
        bottomSheet = findViewById(R.id.cartSheetLinearLayout)
        bottomSheetConstraint = findViewById(R.id.cartSheetConstraintLayoutPeeked)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        mAuth = FirebaseAuth.getInstance()
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
    // onActivityResult function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        val cartButton = findViewById<Button>(R.id.cartButton)
        val cartBar = findViewById<ConstraintLayout>(R.id.cartSheetConstraintLayout)
        val orderButton = findViewById<ExtendedFloatingActionButton>(R.id.cartPlaceOrder)
        cartTotal = findViewById(R.id.cartTotal)
        cartCarbonTotal = findViewById(R.id.cartCarbonTotal)
        cartFragment = CartFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.cartFrameLayout, cartFragment)
            .commit()
        if (data != null) {
            cartData.add(data.getStringExtra("cartInfo"))
            Log.d("Cart Data", cartData.toString())
        }
        updatePrice()
        bottomSheetConstraint.setOnClickListener {
            updatePrice()
        }
        cartBar.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                updatePrice()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                updatePrice()
            }
        }
        cartButton.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                updatePrice()
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                updatePrice()
            }
        }
        orderButton.setOnClickListener {
            updatePrice()
            val orderTotal = cartTotal.text.substring(1)
            val orderCarbonTotal = cartCarbonTotal.text.substring(0, cartCarbonTotal.text.indexOf(" "))
            sendToDB(orderTotal, orderCarbonTotal)
        }
    }
    // Place order from cart function
    private fun sendToDB(orderTotalImport: String, orderCarbonTotalImport: String) {
        val user = mAuth.currentUser
        val date = java.util.Calendar.getInstance()
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (user != null){
            val itemName = arrayListOf<String>()
            val itemQuantity = arrayListOf<String>()
            for (item in cartData){
                val rem = item.substring(item.indexOf("/") + 1, item.length)
                val name = rem.substring(0, rem.indexOf("/"))
                val quantity = rem.substring(rem.indexOf("/") + 1, rem.lastIndexOf("/"))
                itemName.add(name)
                itemQuantity.add(quantity)
            }
            val data = hashMapOf(
                    "OrderTotal" to orderTotalImport,
                    "CarbonTotal" to orderCarbonTotalImport,
                    "Produce" to itemName,
                    "Quantity" to itemQuantity,
                    "Market" to marketTitle,
                    "MarketDate" to marketDate,
                    "Date" to date
            )
            db.collection("User")
                    .document(user.uid)
                    .collection("Orders")
                    .document()
                    .set(data)
        }
        cartData.clear()
        val cartButton = findViewById<Button>(R.id.cartButton)
        val cartSheetConstraintLayout = findViewById<ConstraintLayout>(R.id.cartSheetConstraintLayout)
        val cartTotalTextView = findViewById<TextView>(R.id.cartTotal)
        val cartComplete = findViewById<TextView>(R.id.cartComplete)
        cartButton.isVisible = false
        cartTotalTextView.isVisible = false
        cartSheetConstraintLayout.setBackgroundColor(getColor(R.color.success))
        cartComplete.isVisible = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        Handler().postDelayed({
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            Handler().postDelayed({
                cartComplete.isVisible = false
                cartButton.isVisible = true
                cartTotalTextView.isVisible = true
                cartSheetConstraintLayout.setBackgroundColor(getColor(R.color.colorAccent))
            }, 200)}, 1500)
    }
    // Update price function
    private fun updatePrice() {
        var totalPrice = 0.0
        var totalCarbon = 0.0
        for (item in cartData){
            val price = item.substring(0, item.indexOf("/")).toDouble()
            val carbon = item.substring(item.lastIndexOf("/") + 1, item.length).toDouble()
            totalPrice += price
            totalCarbon += carbon
        }
        val totalPriceFormat = String.format("%.2f", totalPrice)
        val totalCarbonFormat = String.format("%.2f", totalCarbon)
        val cartTotalText = "£$totalPriceFormat"
        val cartCarbonTotalText = "$totalCarbonFormat CO₂e"
        cartTotal.text = cartTotalText
        cartCarbonTotal.text = cartCarbonTotalText
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

