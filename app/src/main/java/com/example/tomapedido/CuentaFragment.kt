package com.example.tomapedido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CuentaFragment : Fragment() {

    private var clienteActual: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cuenta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Obtenemos el nombre de la mesa
        clienteActual = arguments?.getString("nombreCliente") ?: "Desconocido"

        val tvTitulo = view.findViewById<TextView>(R.id.tvTituloCuenta)
        tvTitulo.text = "Cuenta de: $clienteActual"

        // 2. Preparamos el RecyclerView vacío para evitar errores en Logcat
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCuentaDetalle)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CuentaAdapter(emptyList())

        val tvTotalCuenta = view.findViewById<TextView>(R.id.tvTotalCuenta)

        // 3. Hacemos el GET al servidor Linux
        RetrofitClient.instance.getCuentaCliente(clienteActual).enqueue(object : Callback<TicketRequest> {
            override fun onResponse(call: Call<TicketRequest>, response: Response<TicketRequest>) {
                if (response.isSuccessful) {
                    val ticket = response.body()

                    if (ticket != null) {
                        // Inyectamos los datos reales de MongoDB en la interfaz
                        recyclerView.adapter = CuentaAdapter(ticket.productos)
                        tvTotalCuenta.text = "Total: $${ticket.total}"
                    }
                } else {
                    Toast.makeText(requireContext(), "Error al buscar la cuenta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TicketRequest>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // 4. Lógica de los botones
        val btnAgregarMas = view.findViewById<Button>(R.id.btnAgregarMas)
        btnAgregarMas.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nombreCliente", clienteActual)
            findNavController().navigate(R.id.action_cuentaFragment_to_SecondFragment, bundle)
        }

        val btnCerrarCuenta = view.findViewById<Button>(R.id.btnCerrarCuenta)
        btnCerrarCuenta.setOnClickListener {

            RetrofitClient.instance.cobrarCuenta(clienteActual).enqueue(object : Callback<Map<String, String>> {
                override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "¡Cuenta cobrada y cerrada!", Toast.LENGTH_SHORT).show()

                        // Regresamos al mesero a la pantalla inicial de mesas.
                        // OJO: Revisa tu nav_graph.xml, aquí asumo que tu inicio se llama FirstFragment
                        findNavController().navigate(R.id.action_cuentaFragment_to_mesasFragment)
                    } else {
                        Toast.makeText(requireContext(), "Error al cobrar en el servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}