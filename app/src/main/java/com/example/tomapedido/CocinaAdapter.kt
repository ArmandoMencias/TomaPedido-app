package com.example.tomapedido

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CocinaAdapter(
    private var pedidos: List<TicketRequest>,
    private val onComandaListaClick: (TicketRequest) -> Unit
) : RecyclerView.Adapter<CocinaAdapter.CocinaViewHolder>() {

    class CocinaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvClienteCocina: TextView = view.findViewById(R.id.tvClienteCocina)
        val tvEstatusCocina: TextView = view.findViewById(R.id.tvEstatusCocina)
        val rvProductosCocina: RecyclerView = view.findViewById(R.id.rvProductosCocina)
        val btnListo: Button = view.findViewById(R.id.btnListo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido_cocina, parent, false)
        return CocinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CocinaViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.tvClienteCocina.text = pedido.cliente
        holder.tvEstatusCocina.text = "Estatus: ${pedido.status}"

        // Pasamos la lista de platos completa para poder mostrar los nombres de las personas
        holder.rvProductosCocina.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvProductosCocina.adapter = PlatilloCocinaAdapter(pedido.platos)

        holder.btnListo.setOnClickListener {
            onComandaListaClick(pedido)
        }
    }

    override fun getItemCount() = pedidos.size

    fun updatePedidos(newPedidos: List<TicketRequest>) {
        pedidos = newPedidos
        notifyDataSetChanged()
    }
}