package com.example.tomapedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MesasAdapter(
    private val mesas: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MesasAdapter.MesasViewHolder>() {

    class MesasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreCliente: TextView = view.findViewById(R.id.tvNombreCliente)
        // El estado de la comanda ya está fijo en el XML como "Comanda Abierta" por ahora
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa, parent, false)
        return MesasViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesasViewHolder, position: Int) {
        val cliente = mesas[position]
        holder.tvNombreCliente.text = cliente

        // Cuando el mesero toque a "Ana" o "Juan"
        holder.itemView.setOnClickListener {
            onItemClick(cliente)
        }
    }

    override fun getItemCount(): Int = mesas.size
}