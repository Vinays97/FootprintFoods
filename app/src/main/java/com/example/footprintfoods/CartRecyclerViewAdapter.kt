package com.example.footprintfoods

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartRecyclerViewAdapter(cartDataImport: ArrayList<String>) : RecyclerView.Adapter<CartRecyclerViewAdapter.ViewHolder>() {
    // Assign Variables
    val cartData = cartDataImport
    // Inner class
    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        // Initialise card variables
        var deleteItem: Button = itemView.findViewById(R.id.productDetailsMinus)
        var cartTitle: TextView = itemView.findViewById(R.id.cartProductTitle)
        var cartQuantity: TextView = itemView.findViewById(R.id.cartProductQuantity)
        var cartPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        // Initialise onClickListener
        init {
            deleteItem.setOnClickListener {
                val position: Int = adapterPosition
                removeItem(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.cart_cards, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d(ContentValues.TAG, "Added to adapter")
        Log.d(ContentValues.TAG, "Displaying Views")
        val item = cartData[position]
        val price = item.substring(0, item.indexOf("/"))
        val rem = item.substring(item.indexOf("/") + 1, item.length)
        val product = rem.substring(0, rem.indexOf("/"))
        val rem2 = rem.substring(rem.indexOf("/") + 1, rem.length)
        val quantity = rem2.substring(0, rem2.indexOf("/"))
        val carbon = rem2.substring(rem2.indexOf("/") + 1, rem2.length)
        holder.cartTitle.text = product
        holder.cartQuantity.text = quantity
        holder.cartPrice.text = price
    }

    override fun getItemCount(): Int {
        return cartData.size
    }

    fun removeItem(position: Int) {
        cartData.removeAt(position)
        notifyDataSetChanged()
    }
}