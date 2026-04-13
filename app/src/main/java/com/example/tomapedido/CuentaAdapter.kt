package com.example.tomapedido

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CuentaAdapter(private val platos: List<Plato>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1
    private val itemsAMostrar = mutableListOf<Any>()

    init {
        platos.forEach { plato ->
            // Agregamos el nombre de la persona como encabezado
            itemsAMostrar.add(plato.nombre_plato)
            // Agregamos todos sus productos debajo
            itemsAMostrar.addAll(plato.items)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsAMostrar[position] is String) TYPE_HEADER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            // Usamos un layout simple para los nombres de las personas (comensales)
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            // Usamos tu nuevo layout personalizado para los productos
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cuenta, parent, false)
            ProductoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                val nombrePersona = itemsAMostrar[position] as String
                holder.tvHeader.apply {
                    text = nombrePersona.uppercase() // En mayúsculas para que resalte
                    textSize = 14f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(Color.parseColor("#1565C0")) // Azul de tu diseño "Mesero"
                    setPadding(16, 40, 16, 8)
                }
            }
            is ProductoViewHolder -> {
                val producto = itemsAMostrar[position] as Producto

                // 1. Cantidad: Usamos el TextView específico
                val cantReal = if (producto.cantidad <= 0) 1 else producto.cantidad
                holder.tvCantidad.text = "${cantReal}x"

                // 2. Nombre del producto
                holder.tvNombre.text = producto.nombre

                // 3. Ingredientes: Ahora van en su propio TextView debajo
                val listaIngredientes = producto.ingredientes_elegidos ?: emptyList()
                if (listaIngredientes.isNotEmpty()) {
                    holder.tvIngredientes.text = "+ ${listaIngredientes.joinToString(", ")}"
                    holder.tvIngredientes.visibility = View.VISIBLE
                } else {
                    holder.tvIngredientes.visibility = View.GONE
                }

                // 4. Subtotal (Precio * Cantidad) formateado
                val subtotal = cantReal * producto.precio
                holder.tvPrecio.text = String.format("$%.2f", subtotal)
            }
        }
    }

    override fun getItemCount(): Int = itemsAMostrar.size

    // ViewHolder para el nombre de la persona
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(android.R.id.text1)
    }

    // ViewHolder para el producto (Vinculado a tu nuevo item_cuenta.xml)
    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCantidad: TextView = view.findViewById(R.id.tvCantidadProductoCuenta)
        val tvNombre: TextView = view.findViewById(R.id.tvNombreProductoCuenta)
        val tvPrecio: TextView = view.findViewById(R.id.tvPrecioProductoCuenta)
        val tvIngredientes: TextView = view.findViewById(R.id.tvIngredientesCuenta)
    }
}