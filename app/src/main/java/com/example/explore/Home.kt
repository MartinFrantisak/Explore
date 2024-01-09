package com.example.explore

import android.annotation.SuppressLint
import android.content.Context
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cityInput = view.findViewById<EditText>(R.id.city_input)
        val providedTip = view.findViewById<TextView>(R.id.provided_tip)
        val loadingText = view.findViewById<TextView>(R.id.loading_placeholder)

        cityInput?.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Handle the 'Done' action here
                hideKeyboard(v)
                Log.i("CITY_INPUT", cityInput.text.toString())
                true
            } else {
                false
            }
        }

        val confirmCityButton = view.findViewById<Button>(R.id.confirm_city)
        confirmCityButton.setOnClickListener {
            loadingText.visibility = View.VISIBLE
            val userText = "Hi, I want to visit ${cityInput.text}. Write one interesting travel tip in this city with short description (max 2 sentences)."
            providedTip.text = "Loading"
            getResponseFromAPI(userText)
            hideKeyboard(it)
        }

        val nextTipButton = view.findViewById<Button>(R.id.next_tip)
        nextTipButton.setOnClickListener {
            loadingText.visibility = View.VISIBLE
            providedTip.visibility = View.GONE
            getResponseFromAPI("Last time you gave me this travel tip: ${providedTip.text} in ${cityInput.text}. What else should I see in ${cityInput.text}? Follow up with another short travel trip in ${cityInput.text} (max 2 sentences).")
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
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    val generatedText = response.body()?.choices?.firstOrNull()?.message?.content
                    view?.findViewById<TextView>(R.id.provided_tip)?.text = generatedText
                    view?.findViewById<TextView>(R.id.loading_placeholder)?.visibility = View.GONE
                    view?.findViewById<TextView>(R.id.provided_tip)?.visibility = View.VISIBLE
                    view?.findViewById<ScrollView>(R.id.scroll_view_response)?.visibility = View.VISIBLE
                    view?.findViewById<LinearLayout>(R.id.linear_layout_buttons)?.visibility = View.VISIBLE

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("APIError", "Error Code: ${response.code()} - $errorBody")
                }
            }
            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                // TODO: Handle network error
            }
        })
    }
}