package com.example.tomapedido

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("menu")
    fun getMenu(): Call<List<Producto>>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}