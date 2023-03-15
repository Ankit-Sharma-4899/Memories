package com.memories.memories.Adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class DatesAdapter(private val data: List<Date>) : RecyclerView.Adapter<DatesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // bind the data to the view holder
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // define the view holder's views
    }

}