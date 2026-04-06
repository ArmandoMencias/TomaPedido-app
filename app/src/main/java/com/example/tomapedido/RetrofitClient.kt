package com.example.tomapedido
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // CAMBIA ESTA IP por la que te dio 'hostname -I' en tu terminal de Linux
    private const val BASE_URL = "http://192.168.1.133:8000/"

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}