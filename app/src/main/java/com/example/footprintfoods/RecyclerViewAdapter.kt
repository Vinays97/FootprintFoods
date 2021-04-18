package com.example.footprintfoods

import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

class RecyclerViewAdapter(itemTitlesImport: MutableList<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val itemTitles = itemTitlesImport

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var image : ImageView = itemView.findViewById(R.id.marketImage)
        var textTitle: TextView = itemView.findViewById(R.id.marketTitle)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.market_cards, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(TAG, "Added to adapter")
        Log.d(TAG, "Displaying Views")
        holder.textTitle.text = itemTitles[position]
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "itemTitles.size = ${itemTitles.size}")
        return itemTitles.size
    }
}