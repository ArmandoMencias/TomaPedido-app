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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CocinaFragment : Fragment() {

    private var _binding: FragmentCocinaBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CocinaAdapter

    // Variables para el WebSocket
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

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

        // Carga inicial normal
        cargarPedidos()

        // Iniciamos la oreja biónica (WebSocket)
        iniciarWebSocket()
    }

    private fun iniciarWebSocket() {
        // OJO AQUÍ: Pon tu IP real. Nota que empieza con ws:// en lugar de http://
        val ipServidor = "192.168.1.133"
        val request = Request.Builder().url("ws://$ipServidor:8000/ws/cocina").build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                // Cuando FastAPI transmite "NUEVO_PEDIDO"
                if (text == "NUEVO_PEDIDO") {
                    Log.d("WebSocket", "¡Aviso de nuevo pedido recibido!")

                    // El WebSocket corre en segundo plano. Para actualizar la pantalla
                    // o mostrar un Toast, debemos obligarlo a correr en el hilo principal.
                    activity?.runOnUiThread {
                        Toast.makeText(context, "¡Nueva comanda entrante!", Toast.LENGTH_SHORT).show()
                        cargarPedidos() // Recargamos la lista automáticamente
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                Log.e("WebSocket", "Error de conexión: ${t.message}")
            }
        })
    }

    private fun cargarPedidos() {
        Log.d("CocinaFragment", "Iniciando carga de pedidos...")
        RetrofitClient.instance.getTodosLosPedidos().enqueue(object : Callback<List<TicketRequest>> {
            override fun onResponse(call: Call<List<TicketRequest>>, response: Response<List<TicketRequest>>) {
                if (response.isSuccessful) {
                    val pedidos = response.body() ?: emptyList()
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
        val idUnico = pedido.id_ticket ?: run {
            Toast.makeText(context, "Error: El pedido no tiene ID", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.instance.marcarComandaLista(idUnico).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Pedido enviado a meseros", Toast.LENGTH_SHORT).show()
                    cargarPedidos()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Es vital desconectar el WebSocket al salir para no drenar la memoria del dispositivo
        webSocket?.cancel()
        _binding = null
    }
}