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

    @GET("pedidos_activos")
    fun getTodosLosPedidos(): Call<List<TicketRequest>>

    @GET("pedidos/{cliente}")
    fun getCuentaCliente(@Path("cliente") cliente: String): Call<TicketRequest>

    @PUT("pedidos/{cliente}/status")
    fun actualizarEstatusPedido(
        @Path("cliente") cliente: String,
        @Body status: Map<String, String>
    ): Call<Void>

    @PUT("pedidos/{cliente}/cobrar")
    fun cobrarCuenta(@Path("cliente") cliente: String): Call<Map<String, String>>

    // ACTUALIZADO: Ahora devuelve la lista de tickets completos (nombre + status)
    @GET("clientes/activos")
    fun getClientesActivos(): Call<List<TicketRequest>>

    // === ¡LA RUTA NUEVA PARA LA COLA FIFO DE COCINA! ===
    // Esta es la que usa el _id de Mongo para cerrar solo un bloque
    @PUT("comanda/{id_ticket}/listo")
    fun marcarComandaLista(@Path("id_ticket") idTicket: String): Call<Void>
}