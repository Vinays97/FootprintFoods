package com.example.footprintfoods

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(itemTitlesImport: MutableList<String>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private val itemTitles = itemTitlesImport

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var image : ImageView = itemView.findViewById(R.id.marketImage)
        var textTitle: TextView = itemView.findViewById(R.id.marketTitle)
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

        holder.itemView.setOnClickListener{v : View ->
            Toast.makeText(v.context, "Clicked item", Toast.LENGTH_SHORT).show()
        }

    }

    override fun getItemCount(): Int {
        Log.d(TAG, "itemTitles.size = ${itemTitles.size}")
        return itemTitles.size
    }
}