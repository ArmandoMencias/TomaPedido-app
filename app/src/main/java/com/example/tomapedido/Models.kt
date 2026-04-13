package com.example.tomapedido

import com.google.gson.annotations.SerializedName

data class Producto(
    val id_producto: String? = null,
    val categoria: String? = null,
    val nombre: String,
    val precio: Double,
    val disponible: Boolean = true,
    val ingredientes: List<String>? = emptyList(),
    var ingredientes_elegidos: List<String> = emptyList(),
    var cantidad: Int = 1,
    var preparado: Boolean = false // <-- NUEVO: Para control de cocina
)

data class Plato(
    val nombre_plato: String,
    val items: List<Producto>
)

data class TicketRequest(
    @SerializedName("_id") val id_ticket: String? = null, // Enlaza el _id de Mongo con tu variable
    val cliente: String,
    val platos: List<Plato>,
    val total: Double,
    val status: String,
    val status_cocina: String = "pendiente",
    val status_mesero: String = "pendiente",
    val timestamp: Long = System.currentTimeMillis()
)

data class LoginRequest(val username: String, val pin: String)
data class LoginResponse(val status: String, val nombre: String, val rol: String)