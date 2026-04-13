package com.example.tomapedido

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tomapedido.databinding.FragmentCocinaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CocinaFragment : Fragment() {

    private var _binding: FragmentCocinaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocinaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCocinaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CocinaAdapter(emptyList()) { pedido ->
            actualizarEstatusCocina(pedido)
        }

        binding.rvPedidosCocina.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPedidosCocina.adapter = adapter

        cargarPedidos()
    }

    private fun cargarPedidos() {
        Log.d("CocinaFragment", "Iniciando carga de pedidos...")
        RetrofitClient.instance.getTodosLosPedidos().enqueue(object : Callback<List<TicketRequest>> {
            override fun onResponse(call: Call<List<TicketRequest>>, response: Response<List<TicketRequest>>) {
                if (response.isSuccessful) {
                    val pedidos = response.body() ?: emptyList()

                    // 1. Filtramos los que no están listos
                    // 2. ORDENAMOS por timestamp (el más viejo primero) para la cola FIFO
                    val pedidosPendientes = pedidos
                        .filter { it.status_cocina != "listo" }
                        .sortedBy { it.timestamp }

                    adapter.updatePedidos(pedidosPendientes)
                } else {
                    Log.e("CocinaFragment", "Error en respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<TicketRequest>>, t: Throwable) {
                Log.e("CocinaFragment", "Error de red", t)
            }
        })
    }

    private fun actualizarEstatusCocina(pedido: TicketRequest) {
        // Obtenemos el ID único de Mongo. Si por algún error es nulo, cancelamos la acción.
        val idUnico = pedido.id_ticket ?: run {
            Toast.makeText(context, "Error: El pedido no tiene ID", Toast.LENGTH_SHORT).show()
            return
        }

        // USAMOS LA NUEVA RUTA QUE ACTUALIZA POR ID DE MONGO, NO POR NOMBRE DE CLIENTE
        RetrofitClient.instance.marcarComandaLista(idUnico).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Pedido enviado a meseros", Toast.LENGTH_SHORT).show()
                    cargarPedidos() // Recargamos para que el bloque desaparezca de la pantalla
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}