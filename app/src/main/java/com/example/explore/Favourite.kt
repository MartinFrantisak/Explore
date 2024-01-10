package com.example.explore

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Favourite : Fragment() {

    private lateinit var sharedPreferencesStorage: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesStorage = requireActivity().getSharedPreferences("Storage", Context.MODE_PRIVATE)

        val emptyPlaceholder = view.findViewById<TextView>(R.id.emptyPlaceholder)

        val data = getData()
        val dataList = parseData(data)
        viewManager = LinearLayoutManager(activity)
        viewAdapter = Adapter(dataList)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        val deleteAllButton = view.findViewById<Button>(R.id.deleteAll)
        deleteAllButton.setOnClickListener {
            deleteAll()
            (viewAdapter as? Adapter)?.clearData()
            deleteAllButton.visibility = View.GONE
            emptyPlaceholder.visibility = View.VISIBLE
        }
    }

    private fun getData(): String {
        return sharedPreferencesStorage.getString("data", "") ?: ""
    }

    private fun parseData(data: String): MutableList<StorageData> {
        val gson = Gson()
        return if (data.isNotEmpty()) {
            view?.findViewById<Button>(R.id.deleteAll)?.visibility = View.VISIBLE
            val type = object : TypeToken<MutableList<StorageData>>() {}.type
            gson.fromJson(data, type)

        } else {
            view?.findViewById<Button>(R.id.deleteAll)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.emptyPlaceholder)?.visibility = View.VISIBLE
            mutableListOf()
        }
    }

    private fun deleteAll() {
        val editor = sharedPreferencesStorage.edit()
        editor.clear()
        editor.apply()
    }
}