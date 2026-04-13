package com.example.tomapedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Agregamos 'onItemClick' al constructor para capturar el toque en el producto
class MenuAdapter(
    private val productos: List<Producto>,
    private val onItemClick: (Producto) -> Unit
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
        // Si agregaste el TextView del icono (el emoji), descomenta la siguiente línea:
        // val tvIcono: TextView = view.findViewById(R.id.tvIcono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val producto = productos[position]

        holder.tvNombre.text = producto.nombre
        // Usamos una llamada segura (?.) y el operador Elvis (?:) para manejar el valor nulo
        holder.tvCategoria.text = producto.categoria?.uppercase() ?: "GENERAL"
        holder.tvPrecio.text = "$${producto.precio}"

        // --- EL CAMBIO CLAVE ---
        // Al tocar cualquier parte de la "tarjeta" (itemView), ejecutamos la función
        holder.itemView.setOnClickListener {
            onItemClick(producto)
        }
    }

    override fun getItemCount(): Int = productos.size
}