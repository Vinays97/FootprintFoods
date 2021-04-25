package com.example.footprintfoods

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrdersRecyclerViewAdapter(ordersImport: MutableList<String>) : RecyclerView.Adapter<OrdersRecyclerViewAdapter.ViewHolder>() {
    // Initialise variables
    var orders = ordersImport
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        // Initialise card variables
        var marketTitle: TextView = itemView.findViewById(R.id.orderCardTitle)
        var marketDate: TextView = itemView.findViewById(R.id.orderCardMarketDate)
        var orderTotal: TextView = itemView.findViewById(R.id.orderCardTotal)
        var orderCarbon: TextView = itemView.findViewById(R.id.orderCardCarbon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.order_cards, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(ContentValues.TAG, "Added to adapter")
        Log.d(ContentValues.TAG, "Displaying Views")
        val item = orders[position]
        val title = item.substring(0,item.indexOf("/"))
        val rem = item.substring(item.indexOf("/") + 1, item.length)
        val price = "£${rem.substring(0, rem.indexOf("/"))}"
        val rem2 = rem.substring(rem.indexOf("/") + 1, rem.length)
        val carbon = "${rem2.substring(0, rem2.indexOf("/"))} CO₂e"
        val rem3 = rem2.substring(rem2.indexOf("/") + 1, rem2.length)
        val date = rem3.substring(0, rem3.indexOf("/"))
        val rem4 = rem3.substring(rem3.indexOf("/") + 1, rem3.length)
        val produce = rem4.substring(0, rem4.indexOf("/"))
        val quantities = rem4.substring(rem4.indexOf("/") + 1, rem4.length)
        Log.d("Produce Array", produce)
        Log.d("Quantity Array", quantities)
        holder.marketTitle.text = title
        holder.marketDate.text = date
        holder.orderTotal.text = price
        holder.orderCarbon.text = carbon
    }

    override fun getItemCount(): Int {
        return orders.size
    }

}