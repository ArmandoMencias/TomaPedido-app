package com.example.tomapedido

data class Producto(
    val id_producto: String,
    val categoria: String,
    val nombre: String,
    val precio: Double,
    val ingredientes: List<String>,
    val disponible: Boolean
)

data class LoginRequest(
    val username: String,
    val pin: String
)

data class LoginResponse(
    val status: String,
    val nombre: String,
    val rol: String
)