package com.example.explore

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : Fragment() {

    private lateinit var sharedPreferencesStorage: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesStorage = requireActivity().getSharedPreferences("Storage", Context.MODE_PRIVATE)

        val cityInput = view.findViewById<EditText>(R.id.city_input)
        val description = view.findViewById<TextView>(R.id.description)
        val tipName = view.findViewById<TextView>(R.id.tipName)
        val loadingText = view.findViewById<TextView>(R.id.loading_placeholder)

        cityInput?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                confirmCity(loadingText, cityInput, v)
                true
            } else {
                false
            }
        }

        val confirmCityButton = view.findViewById<Button>(R.id.confirm_city)
        confirmCityButton.setOnClickListener {
            confirmCity(loadingText, cityInput, it)
        }

        val nextTipButton = view.findViewById<Button>(R.id.next_tip)
        nextTipButton.setOnClickListener {
            loadingText.visibility = View.VISIBLE
            description.visibility = View.GONE
            tipName.visibility = View.GONE
            getResponseFromAPI("Last time you gave me this travel tip: ${description.text} in ${cityInput.text}. What else should I see in ${cityInput.text}? Follow up with another short travel trip (max 2 sentences) in ${cityInput.text}. Return in this form: '{\"city\":\"cityName\",\"tipName\":\"tipName\",\"description\":\"description\"}'")
        }

        val saveTipButton = view.findViewById<Button>(R.id.save_tip)
        saveTipButton.setOnClickListener {
            val data = StorageData(tipName.text.toString(), description.text.toString())
            val jsonData = Gson().toJson(data)
            appendNewData(jsonData)
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getResponseFromAPI(userInput: String) {
        val messages = listOf(Message("user", userInput))
        val requestData = RequestData(
            model = "gpt-3.5-turbo",
            messages = messages,
            temperature = 1.0,
            max_tokens = 1024,
            top_p = 1.0,
            frequency_penalty = 0.0,
            presence_penalty = 0.0
        )

        RetrofitClient.instance.generateText(requestData).enqueue(object : Callback<ResponseData> {
            @SuppressLint("CutPasteId")
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val json = response.body()?.choices?.firstOrNull()?.message?.content
                    val out = gson.fromJson(json, City::class.java )
                    view?.findViewById<TextView>(R.id.description)?.text = out.description
                    view?.findViewById<TextView>(R.id.tipName)?.text = out.tipName
                    view?.findViewById<TextView>(R.id.loading_placeholder)?.visibility = View.GONE
                    view?.findViewById<TextView>(R.id.description)?.visibility = View.VISIBLE
                    view?.findViewById<TextView>(R.id.tipName)?.visibility = View.VISIBLE
                    view?.findViewById<ScrollView>(R.id.scroll_view_response)?.visibility = View.VISIBLE
                    view?.findViewById<LinearLayout>(R.id.linear_layout_buttons)?.visibility = View.VISIBLE

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("APIError", "Error Code: ${response.code()} - $errorBody")
                }
            }
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
            }
        })
    }

    private fun saveData(data: String) {
        val editor = sharedPreferencesStorage.edit()
        editor?.putString("data", data)
        editor?.apply()
    }

    private fun appendNewData(newData: String) {
        val gson = Gson()
        val existingDataJson = getData()
        val type = object : TypeToken<ArrayList<StorageData>>() {}.type
        val dataList: ArrayList<StorageData> = if (existingDataJson.isNotEmpty()) {
            try {
                gson.fromJson(existingDataJson, type)
            } catch (e: JsonSyntaxException) {
                ArrayList()
            }
        } else {
            ArrayList()
        }

        val newDataObject = gson.fromJson(newData, StorageData::class.java)
        dataList.add(newDataObject)

        val combinedDataJson = gson.toJson(dataList)
        saveData(combinedDataJson)
    }

    private fun getData(): String {
        return sharedPreferencesStorage.getString("data", "") ?: ""
    }

    private fun confirmCity(loadingText: TextView, cityInput: TextView, it: View) {
        loadingText.visibility = View.VISIBLE
        val userText = "Hi, I want to visit ${cityInput.text}. Write one interesting travel tip in this city with short description (max 2 sentences). Return in this form: '{\"city\":\"cityName\",\"tipName\":\"tipName\",\"description\":\"description\"}'"
        getResponseFromAPI(userText)
        hideKeyboard(it)
    }
}