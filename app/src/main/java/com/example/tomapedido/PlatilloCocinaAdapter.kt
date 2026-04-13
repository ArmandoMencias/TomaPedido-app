package com.example.tomapedido

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlatilloCocinaAdapter(private val platos: List<Plato>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_HEADER = 0
    private val VIEW_TYPE_ITEM = 1

    // Creamos una lista plana para el RecyclerView que contiene tanto nombres como productos
    private val itemsDisplay = mutableListOf<Any>()

    init {
        platos.forEach { plato ->
            itemsDisplay.add(plato.nombre_plato) // Agregamos el nombre de la persona
            itemsDisplay.addAll(plato.items)    // Agregamos sus productos
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemsDisplay[position] is String) VIEW_TYPE_HEADER else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_producto_cocina_individual, parent, false)
            ItemViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            val nombrePersona = itemsDisplay[position] as String
            holder.tvHeader.text = nombrePersona
            holder.tvHeader.textSize = 14f
            holder.tvHeader.setTextColor(0xFFBDC3C7.toInt()) // Gris claro
            holder.tvHeader.setTypeface(null, Typeface.BOLD_ITALIC)
            holder.tvHeader.setPadding(20, 10, 0, 0)
        } else if (holder is ItemViewHolder) {
            val producto = itemsDisplay[position] as Producto
            holder.tvDetalle.text = "${producto.cantidad}x ${producto.nombre}"
            
            updateVisual(holder, producto.preparado)

            holder.cbProducto.setOnCheckedChangeListener(null)
            holder.cbProducto.isChecked = producto.preparado
            holder.cbProducto.setOnCheckedChangeListener { _, isChecked ->
                producto.preparado = isChecked
                updateVisual(holder, isChecked)
            }
        }
    }

    private fun updateVisual(holder: ItemViewHolder, isChecked: Boolean) {
        if (isChecked) {
            holder.tvDetalle.paintFlags = holder.tvDetalle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvDetalle.alpha = 0.4f
        } else {
            holder.tvDetalle.paintFlags = holder.tvDetalle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvDetalle.alpha = 1.0f
        }
    }

    override fun getItemCount() = itemsDisplay.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvHeader: TextView = view.findViewById(android.R.id.text1)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbProducto: CheckBox = view.findViewById(R.id.cbProductoCocina)
        val tvDetalle: TextView = view.findViewById(R.id.tvDetalleProductoCocina)
    }
}