package com.example.tomapedido

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Ahora recibimos TicketRequest para saber el estatus de cada mesa
class MesasAdapter(
    private var mesas: List<TicketRequest>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MesasAdapter.MesasViewHolder>() {

    class MesasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreCliente: TextView = view.findViewById(R.id.tvNombreCliente)
        val tvEstadoComanda: TextView = view.findViewById(R.id.tvEstadoComanda)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MesasViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mesa, parent, false)
        return MesasViewHolder(view)
    }

    override fun onBindViewHolder(holder: MesasViewHolder, position: Int) {
        val ticket = mesas[position]
        holder.tvNombreCliente.text = ticket.cliente
        
        // --- CAMBIO VISUAL SEGÚN ESTATUS DE COCINA ---
        if (ticket.status_cocina.equals("listo", ignoreCase = true)) {
            holder.tvEstadoComanda.text = "¡LISTO EN COCINA! ✅"
            holder.tvEstadoComanda.setTextColor(Color.parseColor("#27AE60")) // Verde esmeralda
            holder.tvNombreCliente.setTextColor(Color.parseColor("#27AE60"))
        } else {
            holder.tvEstadoComanda.text = "En preparación... 🕒"
            holder.tvEstadoComanda.setTextColor(Color.GRAY)
            holder.tvNombreCliente.setTextColor(Color.BLACK)
        }

        holder.itemView.setOnClickListener {
            onItemClick(ticket.cliente)
        }
    }

    override fun getItemCount(): Int = mesas.size

    fun updateMesas(newMesas: List<TicketRequest>) {
        mesas = newMesas
        notifyDataSetChanged()
    }
}