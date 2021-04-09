package com.example.footprintfoods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mAuth: FirebaseAuth

    lateinit var homeFragment: HomeFragment
    lateinit var orderFragment: OrderFragment
    lateinit var favoritesFragment: FavoritesFragment
    lateinit var voucherFragment: VoucherFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_home)
        val toolBar = findViewById<Toolbar>(R.id.toolBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val headerView = navView.getHeaderView(0)
        val welcomeText = findViewById<TextView>(R.id.welcomeNameText)
        setSupportActionBar(toolBar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        homeFragment = HomeFragment()
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frameLayout, homeFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mItem1 -> {
                    homeFragment = HomeFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, homeFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.mItem2 -> {
                    orderFragment = OrderFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, orderFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.mItem3 -> {
                    favoritesFragment = FavoritesFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, favoritesFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.mItem4 -> {
                    voucherFragment = VoucherFragment()
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frameLayout, voucherFragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }
                R.id.nav_logout -> signOut()
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val userImage = headerView.findViewById<ImageView>(R.id.userImage)
        val userName = headerView.findViewById<TextView>(R.id.userName)
        val firstName = currentUser?.displayName?.split(" ")?.getOrNull(0)
        userName.text = currentUser?.displayName
        welcomeText.text = ("Hey, $firstName")
        Glide.with(this).load(currentUser?.photoUrl).into(userImage)

    }

    private fun signOut() {
        mAuth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}