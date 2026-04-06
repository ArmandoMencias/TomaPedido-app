package com.example.tomapedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// 1. Cambiamos List<String> por List<Producto>
class CuentaAdapter(private val productos: List<Producto>) :
    RecyclerView.Adapter<CuentaAdapter.CuentaViewHolder>() {

    class CuentaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProductoCuenta)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecioProductoCuenta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuentaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cuenta, parent, false)
        return CuentaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuentaViewHolder, position: Int) {
        val producto = productos[position]

        // 2. Asignamos los datos reales
        holder.tvNombre.text = producto.nombre
        holder.tvPrecio.text = "$${producto.precio}" // <--- Aquí mostramos el precio real
    }

    override fun getItemCount(): Int = productos.size
}