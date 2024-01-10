package com.example.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class Adapter(private val dataList: MutableList<StorageData>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.cityTextView.text = "${item.city}:"
        holder.tipNameTextView.text = item.tipName
        holder.descriptionTextView.text = item.description
    }
    override fun getItemCount() = dataList.size
     fun clearData() {
        dataList.clear()
        notifyDataSetChanged()
    }
}