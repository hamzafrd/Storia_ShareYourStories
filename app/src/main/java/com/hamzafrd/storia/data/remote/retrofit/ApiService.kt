package com.hamzafrd.storia.data.remote.retrofit

import com.hamzafrd.storia.data.remote.response.DetailResponse
import com.hamzafrd.storia.data.remote.response.ListStoryItem
import com.hamzafrd.storia.data.remote.response.ListStoryResponse
import com.hamzafrd.storia.data.remote.response.LoginResponse
import com.hamzafrd.storia.data.remote.response.MainResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?,
    ): Call<MainResponse>

    @FormUrlEncoded
    @POST("/v1/register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<MainResponse>

    @FormUrlEncoded
    @POST("/v1/login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("/v1/stories")
    fun getStories(): Call<List<ListStoryItem>>

    @GET("/v1/stories")
    suspend fun getPagingStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ListStoryResponse

    @GET("v1/stories/{id}")
    fun getDetailStories(
        @Path("id") id: String
    ): Call<DetailResponse>

    @GET("v1/stories?location=1")
    fun getLocationStories(): Call<ListStoryResponse>
}

