package com.example.tomapedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    // Esta clase "sujeta" los elementos de tu diseño item_producto
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProducto)
        val tvCategoria: TextView = view.findViewById(R.id.tvCategoria)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val producto = productos[position]
        holder.tvNombre.text = producto.nombre
        holder.tvCategoria.text = producto.categoria.uppercase()
        holder.tvPrecio.text = "$${producto.precio}"
    }

    override fun getItemCount(): Int = productos.size
}