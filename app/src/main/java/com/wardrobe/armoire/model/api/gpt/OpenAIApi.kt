package com.wardrobe.armoire.model.api.gpt



import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface OpenAIApi {
    @POST("v1/chat/completions")
    fun getVisionRecommendation(@Body request: GptVisionRequest): Call<GptVisionResponse>


    @POST("v1/images/generations")
    fun createImage(@Body request: DalleRequest): Call<DalleResponse>
}