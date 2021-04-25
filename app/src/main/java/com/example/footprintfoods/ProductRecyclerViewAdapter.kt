package com.example.footprintfoods

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductRecyclerViewAdapter(marketProduceImport: MutableList<String>, marketTitleImport: String, productCategoryImport: String, contextImport: Context) : RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>() {
    // Assign Variables
    private val marketProduce = marketProduceImport
    private val marketTitle = marketTitleImport
    private val productCategory = productCategoryImport
    private val context = contextImport
    // Inner class
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        // Initialise card variables
        var image: ImageView = itemView.findViewById(R.id.productImage)
        var titleText: TextView = itemView.findViewById(R.id.productTitle)
        var priceText: TextView = itemView.findViewById(R.id.productPrice)
        var pricePerUnitText: TextView = itemView.findViewById(R.id.productPricePerUnit)
        var co2Text: TextView = itemView.findViewById(R.id.productCO2Cost)
        // Initialise onClickListener
        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val prodTitle = marketProduce[position].substring(0, marketProduce[position].indexOf("/"))
                Log.d(ContentValues.TAG, "Product Title: $prodTitle")
                val rem = marketProduce[position].substring(marketProduce[position].indexOf("/") + 1, marketProduce[position].length)
                Log.d(ContentValues.TAG, "Rem: $rem")
                val prodCarbon = "${rem.substring(0, rem.indexOf("/"))} CO\u2082e"
                Log.d(ContentValues.TAG, "Product Carbon: $prodCarbon")
                val rem2 = rem.substring(rem.indexOf("/") + 1, rem.length)
                Log.d(ContentValues.TAG, "Rem2: $rem2")
                val prodPrice = "£${rem2.substring(0, rem2.indexOf("/"))}/kg"
                Log.d(ContentValues.TAG, "Product Price: $prodPrice")
                val url = rem2.substring(rem2.indexOf("/") + 1, rem2.length - 1)
                Log.d(ContentValues.TAG, "URL: $url")
                val requestCode = 1
                val intent = Intent(context, ProductActivity::class.java)
                intent.putExtra("prodTitle", prodTitle)
                intent.putExtra("prodCarbon", prodCarbon)
                intent.putExtra("prodPrice", prodPrice)
                intent.putExtra("url", url)
                intent.putExtra("marketTitle", marketTitle)
                intent.putExtra("productCategory", productCategory)
                intent.putExtra("transitionNameImage", R.string.product_card_image.toString())
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        v.context as Activity,
                        image,
                        R.string.product_card_image.toString())
                (context as Activity).startActivityForResult(intent, requestCode, options.toBundle())
            }
        }
    }
    // onCreateViewHolder function
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.product_cards, parent, false)
        return ViewHolder(v)
    }
    // onBindViewHolder function
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(ContentValues.TAG, "Added to adapter")
        Log.d(ContentValues.TAG, "Displaying Views")
        // De-concatenate string
        val prodTitle = marketProduce[position].substring(0, marketProduce[position].indexOf("/"))
        Log.d(ContentValues.TAG, "Product Title: $prodTitle")
        val rem = marketProduce[position].substring(marketProduce[position].indexOf("/") + 1, marketProduce[position].length)
        Log.d(ContentValues.TAG, "Rem: $rem")
        val prodCarbon = "${rem.substring(0, rem.indexOf("/"))} CO\u2082e"
        Log.d(ContentValues.TAG, "Product Carbon: $prodCarbon")
        val rem2 = rem.substring(rem.indexOf("/") + 1, rem.length)
        Log.d(ContentValues.TAG, "Rem2: $rem2")
        val prodPrice = "£${rem2.substring(0, rem2.indexOf("/"))}/kg"
        Log.d(ContentValues.TAG, "Product Price: $prodPrice")
        val url = rem2.substring(rem2.indexOf("/") + 1, rem2.length - 1)
        Log.d(ContentValues.TAG, "URL: $url")
        // Set values
        holder.titleText.text = prodTitle
        holder.co2Text.text = prodCarbon
        holder.priceText.text = prodPrice
        Glide.with(holder.itemView).load(url).into(holder.image)
    }
    // getItemCount function
    override fun getItemCount(): Int {
        Log.d(ContentValues.TAG, "itemTitles.size = ${marketProduce.size}")
        return marketProduce.size
    }
}