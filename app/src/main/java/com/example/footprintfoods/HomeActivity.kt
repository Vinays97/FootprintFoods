package com.example.footprintfoods

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : AppCompatActivity() {
    // Setup Firebase variables
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore
    // Setup fragment variables
    lateinit var homeFragment: HomeFragment
    lateinit var orderFragment: OrderFragment
    lateinit var favoritesFragment: FavoritesFragment
    lateinit var voucherFragment: VoucherFragment
    lateinit var marketFragment: MarketFragment
    lateinit var calendarFragment: CalendarFragment
    // Setup button variables
    private lateinit var btnMarket: Button
    private lateinit var btnOrder: Button
    private lateinit var btnCalendar: Button
    private lateinit var btnSearch: Button
    // Setup variables for DB pull
    public var itemTitles: MutableList<String> = ArrayList()
    public var itemURLs: MutableList<String> = ArrayList()
    public var itemDates: MutableList<String> = ArrayList()
    public var orders: MutableList<String> = ArrayList()
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme & views
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_home)
        // Initialise variables
        val toolBar = findViewById<Toolbar>(R.id.homeToolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val headerView = navView.getHeaderView(0)
        val welcomeText = findViewById<TextView>(R.id.welcomeNameText)
        mAuth = FirebaseAuth.getInstance()
        // Setup toolbar
        setSupportActionBar(toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        // Pull from DB
        databasePull()
        databasePullOrders()
        // Setup navigation drawer toggle
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // Assign buttons
        btnMarket = findViewById(R.id.btnMarket)
        btnOrder = findViewById(R.id.btnOrder)
        btnCalendar = findViewById(R.id.btnCalendar)
        btnSearch = findViewById(R.id.btnSearch)
        // Assign fragments
        homeFragment = HomeFragment()
        orderFragment = OrderFragment()
        favoritesFragment = FavoritesFragment()
        voucherFragment = VoucherFragment()
        marketFragment = MarketFragment()
        calendarFragment = CalendarFragment()
        // Set home fragment
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.homeFrameLayout, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        // Setting button styles programmatically -_-
        resetHeaderButtons()
        // Market button onClick
        btnMarket.setOnClickListener{
            btnMarket.isActivated = true
            btnOrder.isActivated = false
            btnCalendar.isActivated = false
            btnSearch.setText(R.string.search_bar_market)
            btnSearch.isVisible = true
            Handler().postDelayed({ marketClick() }, 200)
        }
        // Order button onClick
        btnOrder.setOnClickListener{
            btnMarket.isActivated = false
            btnOrder.isActivated = true
            btnCalendar.isActivated = false
            btnSearch.setText(R.string.search_bar_order)
            btnSearch.isVisible = true
            Handler().postDelayed({ orderClick() }, 200)
        }
        // Calendar button onClick
        btnCalendar.setOnClickListener{
            btnMarket.isActivated = false
            btnOrder.isActivated = false
            btnCalendar.isActivated = true
            btnSearch.setText(R.string.search_bar_calendar)
            btnSearch.isVisible = true
            Handler().postDelayed({ calendarClick() }, 200)
        }
        // Navigation drawer onClick
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mItem1 -> {
                    resetHeaderButtons()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.homeFrameLayout, homeFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                }
                R.id.mItem2 -> {
                    btnMarket.isActivated = false
                    btnOrder.isActivated = true
                    btnCalendar.isActivated = false
                    btnSearch.setText(R.string.search_bar_order)
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.homeFrameLayout, orderFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                }
                R.id.mItem3 -> {
                    resetHeaderButtons()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.homeFrameLayout, favoritesFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                }
                R.id.mItem4 -> {
                    resetHeaderButtons()
                    supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.homeFrameLayout, voucherFragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                }
                R.id.nav_logout -> signOut()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Get user details from Firebase & save in variables
        val currentUser = mAuth.currentUser
        val userImage = headerView.findViewById<ImageView>(R.id.userImage)
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val firstName = currentUser?.displayName?.split(" ")?.getOrNull(0)
        // Set user details in texts/image
        userName.text = currentUser?.displayName
        welcomeText.text = ("Hey, $firstName")
        Glide.with(this).load(currentUser?.photoUrl).into(userImage)
    }

    override fun onRestart() {
        super.onRestart()
        databasePullOrders()
        databasePull()
    }

    // Calendar click function
    private fun calendarClick() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.homeFrameLayout, calendarFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    // Order click function
    private fun orderClick() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.homeFrameLayout, orderFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    // Market click function
    private fun marketClick() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.homeFrameLayout, marketFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    // Reset colours function
    private fun resetHeaderButtons() {
        btnMarket.isActivated = false
        btnOrder.isActivated = false
        btnCalendar.isActivated = false
        btnSearch.setText(R.string.search_bar_text)
        btnSearch.isVisible = false
    }

    // Sign out function
    private fun signOut() {
        mAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
    // Toggle drawer icon function
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    // Database pull market function
    private fun databasePull() {
        itemTitles.clear()
        itemURLs.clear()
        itemDates.clear()
        db.collection("Foodmarkets")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    Log.d(ContentValues.TAG, "Starting pulling from DB")
                    for (document in result) {
                        itemTitles.add(document.id)
                        itemURLs.add(document.get("photoURL").toString())
                        val itemDate: Timestamp = document.get("Date") as Timestamp
                        val milli = itemDate.seconds * 1000 + itemDate.nanoseconds / 1000000
                        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
                        val netDate = Date(milli)
                        val date = sdf.format(netDate)
                        itemDates.add(date)
                    }
                    Log.d(ContentValues.TAG, "Done from DB")
                    Log.d(ContentValues.TAG, "itemTitles = $itemTitles")
                    Log.d(ContentValues.TAG, "itemURLs = $itemURLs")
                    Log.d(ContentValues.TAG, "itemDates = $itemDates")
                } else {
                    Log.d(ContentValues.TAG, "No results")
                }
            }
    }
    // Database pull orders function
    private fun databasePullOrders(){
        val user = mAuth.currentUser
        orders.clear()
        if (user != null){
            db.collection("User")
                    .document(user.uid)
                    .collection("Orders")
                    .orderBy("Date", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener { result ->
                        if (result != null){
                            Log.d(ContentValues.TAG, "Starting pulling from DB")
                            for (document in result){
                                val marketTitle = document.get("Market").toString()
                                val marketDate = document.get("MarketDate").toString()
                                val orderTotal = document.get("OrderTotal").toString()
                                val carbonTotal = document.get("CarbonTotal").toString()
                                val produce = document.get("Produce").toString()
                                val quantity = document.get("Quantity").toString()
                                val concat = "$marketTitle/$orderTotal/$carbonTotal/$marketDate/$produce/$quantity"
                                orders.add(concat)
                                Log.d("Orders: ", concat)
                            }
                        } else {
                            Log.d(ContentValues.TAG, "No results")
                        }
                    }
        }
    }
}
