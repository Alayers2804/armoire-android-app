package com.wardrobe.armoire.model.api.gpt



import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


interface RetrofitConfig {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getVisionRecommendation(@Body request: GptVisionRequest): Call<GptVisionResponse>
}