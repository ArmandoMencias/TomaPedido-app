package com.example.tomapedido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tomapedido.databinding.FragmentSecondBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val listaPedidoTemporal = mutableListOf<Producto>()

    // Variable para guardar el nombre del cliente que viene del Fragmento anterior
    private var clienteActual: String = "Cliente Nuevo"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recibimos el nombre desde el Bundle
        clienteActual = arguments?.getString("nombreCliente") ?: "Cliente Nuevo"
        Toast.makeText(requireContext(), "Atendiendo cuenta de: $clienteActual", Toast.LENGTH_SHORT).show()

        val recyclerView = binding.rvMenu
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        RetrofitClient.instance.getMenu().enqueue(object : Callback<List<Producto>> {
            override fun onResponse(call: Call<List<Producto>>, response: Response<List<Producto>>) {
                if (response.isSuccessful) {
                    val listaProductos = response.body() ?: emptyList()

                    recyclerView.adapter = MenuAdapter(listaProductos) { productoSeleccionado ->
                        listaPedidoTemporal.add(productoSeleccionado)
                        Toast.makeText(context, "${productoSeleccionado.nombre} agregado", Toast.LENGTH_SHORT).show()

                        if(listaPedidoTemporal.size == 1) {
                            mostrarDialogoConfirmacion()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                Toast.makeText(context, "Error al cargar menú: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 2. Simplificamos el Dialog porque ya sabemos de quién es la cuenta
    private fun mostrarDialogoConfirmacion() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar Pedido")
        builder.setMessage("¿Enviar pedido a la cocina para $clienteActual?")

        builder.setPositiveButton("Enviar a Cocina") { _, _ ->
            enviarPedidoAlServidor(clienteActual)
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun enviarPedidoAlServidor(nombreCliente: String) {
        val totalPedido = listaPedidoTemporal.sumOf { it.precio }

        // Ya no hacemos el .map { it.nombre }. Pasamos la lista directa.
        val pedido = TicketRequest(
            cliente = nombreCliente,
            productos = listaPedidoTemporal.toList(), // <--- Mandamos el objeto completo
            total = totalPedido,
            status = "pendiente"
        )

        RetrofitClient.instance.crearPedido(pedido).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Pedido de $nombreCliente enviado a cocina", Toast.LENGTH_LONG).show()
                    listaPedidoTemporal.clear()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}