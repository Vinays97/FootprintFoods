package com.example.footprintfoods

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView

class ProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set theme & views
        setTheme(R.style.Theme_FootprintFoods)
        setContentView(R.layout.activity_product)
        // Pull from DB
        databasePull()
        // Initialise Variables
        val prodImage = findViewById<ImageView>(R.id.productDetailsImage)
        val backButton = findViewById<ImageView>(R.id.productDetailsBackButton)
        val prodTitle = findViewById<TextView>(R.id.productDetailsTitle)
        val prodDesc = findViewById<TextView>(R.id.productDetailsDescription)
        val prodFarmers = findViewById<RadioGroup>(R.id.productDetailsFarmerRadio)
        val prodUsage = findViewById<TextView>(R.id.productDetailsUsageDescription)
        // onClickListener for back button
        backButton.setOnClickListener{
            finish()
        }
    }

    private fun databasePull() {
        TODO("Not yet implemented")
    }
}