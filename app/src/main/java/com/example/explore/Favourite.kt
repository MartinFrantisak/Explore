package com.example.explore

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
class Favourite : Fragment() {

    private lateinit var sharedPreferencesStorage: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesStorage = requireActivity().getSharedPreferences("Storage", Context.MODE_PRIVATE)

        val data = getData()
        Log.i("Data", "Storage data: $data")
    }

    private fun getData(): String {
        return sharedPreferencesStorage.getString("data", "") ?: ""
    }
}