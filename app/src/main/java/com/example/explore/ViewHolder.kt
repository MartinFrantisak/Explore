package com.example.explore

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val cityTextView: TextView = view.findViewById(R.id.city)
    val tipNameTextView: TextView = view.findViewById(R.id.tipName)
    val descriptionTextView: TextView = view.findViewById(R.id.description)
}