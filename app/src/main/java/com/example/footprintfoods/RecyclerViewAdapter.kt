package com.example.footprintfoods

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewAdapter(itemTitlesImport: MutableList<String>, itemURLsImport: MutableList<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    // Assign Variables
    private val itemTitles = itemTitlesImport
    private val itemURLs = itemURLsImport
    // Inner class
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        // Initialise variables
        var image : ImageView = itemView.findViewById(R.id.marketImage)
        var textTitle: TextView = itemView.findViewById(R.id.marketTitle)
        // Initialise onClickListener
        init {
            itemView.setOnClickListener{ v: View ->
                val position: Int = adapterPosition
                val itemTitle: String = itemTitles[position]
                val intent = Intent(v.context, MarketActivity::class.java)
                intent.putExtra("itemTitle", itemTitle)
                v.context.startActivity(intent)
            }
        }
    }
    // onCreateViewHolder function
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.market_cards, parent, false)
        return ViewHolder(v)
    }
    // onBindViewHolder function
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Added to adapter")
        Log.d(TAG, "Displaying Views")
        val url = itemURLs[position]
        holder.textTitle.text = itemTitles[position]
        Glide.with(holder.itemView).load(url).into(holder.image)
    }
    // getItemCount function
    override fun getItemCount(): Int {
        Log.d(TAG, "itemTitles.size = ${itemTitles.size}")
        return itemTitles.size
    }
}