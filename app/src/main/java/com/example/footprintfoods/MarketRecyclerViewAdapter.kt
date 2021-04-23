package com.example.footprintfoods

import android.app.Activity
import android.app.ActivityOptions
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class MarketRecyclerViewAdapter(itemTitlesImport: MutableList<String>, itemURLsImport: MutableList<String>, itemDatesImport: MutableList<String>) : RecyclerView.Adapter<MarketRecyclerViewAdapter.ViewHolder>() {
    // Assign Variables
    private val itemTitles = itemTitlesImport
    private val itemURLs = itemURLsImport
    private val itemDates = itemDatesImport
    // Inner class
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        // Initialise card variables
        var image : ImageView = itemView.findViewById(R.id.marketImage)
        var textTitle: TextView = itemView.findViewById(R.id.marketTitle)
        var textDate: TextView = itemView.findViewById(R.id.marketDate)
        var marketCards: View = itemView.findViewById(R.id.marketCards)
        // Initialise onClickListener
        init {
            itemView.setOnClickListener{ v: View ->
                val position: Int = adapterPosition
                val itemTitle: String = itemTitles[position]
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(itemDates[position])
                val dateString = SimpleDateFormat("EEEE", Locale.ENGLISH).format(sdf)
                val dateDay = SimpleDateFormat("dd", Locale.ENGLISH).format(sdf)
                val dateMonth = SimpleDateFormat("MMMM", Locale.ENGLISH).format(sdf)
                val concat = "$dateString $dateDay $dateMonth"
                val intent = Intent(v.context, MarketActivity::class.java)
                intent.putExtra("itemTitle", itemTitle)
                intent.putExtra("itemDate", concat)
                intent.putExtra("transitionNameImage", R.string.market_card_image.toString())
                intent.putExtra("transitionNameTitle", R.string.market_card_title.toString())
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        v.context as Activity,
                        Pair(image, R.string.market_card_image.toString()) as Pair<View, String>,
                        Pair(marketCards, R.string.market_card_title.toString()) as Pair<View, String>)
                v.context.startActivity(intent, options.toBundle())
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
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(itemDates[position])
        val dateString = SimpleDateFormat("EE", Locale.ENGLISH).format(sdf)
        val dateDay = SimpleDateFormat("dd", Locale.ENGLISH).format(sdf)
        val dateMonth = SimpleDateFormat("MMMM", Locale.ENGLISH).format(sdf)
        val concat = "$dateString $dateDay $dateMonth"
        val url = itemURLs[position]
        Glide.with(holder.itemView).load(url).into(holder.image)
        holder.textTitle.text = itemTitles[position]
        holder.textDate.text = concat
    }
    // getItemCount function
    override fun getItemCount(): Int {
        Log.d(TAG, "itemTitles.size = ${itemTitles.size}")
        return itemTitles.size
    }
}