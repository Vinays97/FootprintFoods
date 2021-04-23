package com.example.footprintfoods

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DairyFragment : Fragment() {
    // Initialise RecyclerView variables
    private var layoutManager : RecyclerView.LayoutManager? = null
    private var adapter : RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>? = null
    // onCreate function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    // onCreateView function
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dairy, container, false)
    }
    // onViewCreated function
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialise Variables
        val marketProduce = (activity as MarketActivity).marketProduceDairy
        // Initialise layoutManager
        val recyclerView = view.findViewById<RecyclerView>(R.id.marketDairyRecyclerView)
        layoutManager = LinearLayoutManager(activity)
        if (recyclerView != null) {
            recyclerView.layoutManager = layoutManager
        }
        Log.d(ContentValues.TAG, "Sending to Adapter")
        adapter = ProductRecyclerViewAdapter(marketProduce)
        if (recyclerView != null) {
            recyclerView.adapter = adapter
        }
    }
}