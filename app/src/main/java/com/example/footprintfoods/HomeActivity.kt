package com.example.footprintfoods

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    // Initialise Firebase variables
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore
    // Initialise fragment variables
    lateinit var homeFragment: HomeFragment
    lateinit var orderFragment: OrderFragment
    lateinit var favoritesFragment: FavoritesFragment
    lateinit var voucherFragment: VoucherFragment
    lateinit var marketFragment: MarketFragment
    lateinit var calendarFragment: CalendarFragment
    // Initialise button variables
    private lateinit var btnMarket: Button
    private lateinit var btnOrder: Button
    private lateinit var btnCalendar: Button
    private lateinit var btnSearch: Button
    // Array for firebase DB
    public val itemTitles: MutableList<String> = ArrayList()

    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme & views
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_home)
        // Initialise variables
        val toolBar = findViewById<Toolbar>(R.id.toolBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val headerView = navView.getHeaderView(0)
        val welcomeText = findViewById<TextView>(R.id.welcomeNameText)
        // Setup toolbar
        setSupportActionBar(toolBar)
        // Pull from DB
        databasePull()
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
                .replace(R.id.frameLayout, homeFragment)
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
            Handler().postDelayed({marketClick()}, 200)
        }
        // Order button onClick
        btnOrder.setOnClickListener{
            btnMarket.isActivated = false
            btnOrder.isActivated = true
            btnCalendar.isActivated = false
            btnSearch.setText(R.string.search_bar_order)
            btnSearch.isVisible = true
            Handler().postDelayed({orderClick()}, 200)
        }
        // Calendar button onClick
        btnCalendar.setOnClickListener{
            btnMarket.isActivated = false
            btnOrder.isActivated = false
            btnCalendar.isActivated = true
            btnSearch.setText(R.string.search_bar_calendar)
            btnSearch.isVisible = true
            Handler().postDelayed({calendarClick()}, 200)
        }
        // Navigation drawer onClick
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mItem1 -> {
                    resetHeaderButtons()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, homeFragment)
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
                        .replace(R.id.frameLayout, orderFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit()
                }
                R.id.mItem3 -> {
                    resetHeaderButtons()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, favoritesFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit()
                }
                R.id.mItem4 -> {
                    resetHeaderButtons()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, voucherFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit()
                }
                R.id.nav_logout -> signOut()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Get user details from Firebase & save in variables
        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser
        val userImage = headerView.findViewById<ImageView>(R.id.userImage)
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val firstName = currentUser?.displayName?.split(" ")?.getOrNull(0)
        // Set user details in texts/image
        userName.text = currentUser?.displayName
        welcomeText.text = ("Hey, $firstName")
        Glide.with(this).load(currentUser?.photoUrl).into(userImage)
    }

    // Calendar click function
    private fun calendarClick() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, calendarFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    // Order click function
    private fun orderClick() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, orderFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }

    // Market click function
    private fun marketClick() {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, marketFragment)
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
    // Database pull function
    private fun databasePull() {
        itemTitles.clear()
        db.collection("Foodmarkets")
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    Log.d(ContentValues.TAG, "Starting pulling from DB")
                    for (document in result) {
                        itemTitles.add(document.id)
                        Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    }
                    Log.d(ContentValues.TAG, "Done from DB")
                    Log.d(ContentValues.TAG, "itemTitles = $itemTitles")
                } else {
                    Log.d(ContentValues.TAG, "No results")
                }
            }
    }
}
