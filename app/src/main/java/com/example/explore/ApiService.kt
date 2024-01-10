package com.example.explore

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST



interface ApiService {
    @POST("v1/chat/completions")
    @Headers("Authorization: Bearer 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")
    fun generateText(@Body requestData: RequestData): Call<ResponseData>
}

data class ResponseData(val choices: List<Choice>)

data class City(val city: String, val tipName: String, val description: String)

data class Choice(val message: Message)

data class RequestData(
    val model: String,
    val messages: List<Message>,
    val temperature: Double,
    val max_tokens: Int,
    val top_p: Double,
    val frequency_penalty: Double,
    val presence_penalty: Double
)

data class StorageData(
    val city: String,
    val tipName: String,
    val description: String
)

data class Message(
    val role: String,
    val content: String
)
