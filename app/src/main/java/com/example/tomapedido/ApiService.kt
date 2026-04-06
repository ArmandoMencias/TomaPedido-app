package com.example.tomapedido

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PUT

interface ApiService {
    @GET("menu")
    fun getMenu(): Call<List<Producto>>

    @POST("login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("pedidos")
    fun crearPedido(@Body pedido: TicketRequest): Call<Void>

    @GET("pedidos/{cliente}")
    fun getCuentaCliente(@Path("cliente") cliente: String): Call<TicketRequest>

    @PUT("pedidos/{cliente}/cobrar")
    fun cobrarCuenta(@Path("cliente") cliente: String): Call<Map<String, String>>

    @GET("clientes/activos")
    fun getClientesActivos(): Call<List<String>>
}