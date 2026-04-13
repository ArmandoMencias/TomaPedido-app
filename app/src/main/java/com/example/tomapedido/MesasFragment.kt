package com.example.tomapedido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MesasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mesas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMesasActivas)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // --- FUNCIÓN PARA CARGAR DATOS ---
        fun cargarMesas() {
            // CAMBIADO: Ahora esperamos una lista de TicketRequest en lugar de Strings
            RetrofitClient.instance.getClientesActivos().enqueue(object : Callback<List<TicketRequest>> {
                override fun onResponse(call: Call<List<TicketRequest>>, response: Response<List<TicketRequest>>) {
                    if (response.isSuccessful) {
                        val listaReal = response.body() ?: emptyList()
                        // El MesasAdapter ahora puede usar el estatus para mostrar el color verde
                        recyclerView.adapter = MesasAdapter(listaReal) { cliente ->
                            val bundle = Bundle().apply { putString("nombreCliente", cliente) }
                            findNavController().navigate(R.id.action_mesasFragment_to_cuentaFragment, bundle)
                        }
                    }
                }
                override fun onFailure(call: Call<List<TicketRequest>>, t: Throwable) {
                    Toast.makeText(context, "Error de red", Toast.LENGTH_SHORT).show()
                }
            })
        }

        cargarMesas() // Carga inicial

        // --- BOTÓN AÑADIR (+) ---
        val fabNuevaComanda = view.findViewById<FloatingActionButton>(R.id.fabNuevaComanda)
        fabNuevaComanda.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Nueva Mesa")
            val input = android.widget.EditText(context)
            builder.setView(input)

            builder.setPositiveButton("Crear") { _, _ ->
                val nuevoCliente = input.text.toString()
                if (nuevoCliente.isNotEmpty()) {
                    val bundle = Bundle().apply { putString("nombreCliente", nuevoCliente) }
                    findNavController().navigate(R.id.action_mesasFragment_to_cuentaFragment, bundle)
                }
            }
            builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }
}