package com.example.footprintfoods

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navView)
        val headerView = navView.getHeaderView(0)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.mItem1 -> Toast.makeText(applicationContext,
                    "Clicked item 1", Toast.LENGTH_SHORT).show()
                R.id.mItem2 -> Toast.makeText(applicationContext,
                    "Clicked item 2", Toast.LENGTH_SHORT).show()
                R.id.mItem3 -> Toast.makeText(applicationContext,
                    "Clicked item 3", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> signOut()
            }
            true
        }

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        val userImage = headerView.findViewById<ImageView>(R.id.userImage)
        val userName = headerView.findViewById<TextView>(R.id.userName)
        userName.text = currentUser?.displayName
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