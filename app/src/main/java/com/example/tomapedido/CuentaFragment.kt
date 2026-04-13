package com.example.tomapedido

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton // Usamos MaterialButton para el nuevo diseño
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CuentaFragment : Fragment() {

    private var clienteActual: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout fragment_cuenta.xml que actualizamos anteriormente
        return inflater.inflate(R.layout.fragment_cuenta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar el nombre del cliente
        clienteActual = arguments?.getString("nombreCliente") ?: "Desconocido"

        // Configurar el título con el nombre del cliente
        val tvTitulo = view.findViewById<TextView>(R.id.tvTituloCuenta)
        tvTitulo.text = "Cuenta de: $clienteActual"

        // Configurar el RecyclerView (el ticket de productos)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCuentaDetalle)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CuentaAdapter(emptyList())

        val tvTotalCuenta = view.findViewById<TextView>(R.id.tvTotalCuenta)

        // 1. Cargar datos desde el servidor
        obtenerDatosCuenta(recyclerView, tvTotalCuenta)

        // 2. Botón "Agregar Más" (Ahora es un MaterialButton con icono)
        val btnAgregarMas = view.findViewById<MaterialButton>(R.id.btnAgregarMas)
        btnAgregarMas.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("nombreCliente", clienteActual)
            findNavController().navigate(R.id.action_cuentaFragment_to_SecondFragment, bundle)
        }

        // 3. Botón "Cerrar Cuenta" (Con diálogo de confirmación mejorado)
        val btnCerrarCuenta = view.findViewById<MaterialButton>(R.id.btnCerrarCuenta)
        btnCerrarCuenta.setOnClickListener {
            mostrarDialogoConfirmacion()
        }
    }

    private fun obtenerDatosCuenta(recyclerView: RecyclerView, tvTotalCuenta: TextView) {
        RetrofitClient.instance.getCuentaCliente(clienteActual).enqueue(object : Callback<TicketRequest> {
            override fun onResponse(call: Call<TicketRequest>, response: Response<TicketRequest>) {
                if (isAdded && response.isSuccessful) {
                    val ticket = response.body()
                    if (ticket != null) {
                        val listaPlatosSegura = ticket.platos ?: emptyList()
                        recyclerView.adapter = CuentaAdapter(listaPlatosSegura)
                        // Formateamos el total para que se vea profesional
                        tvTotalCuenta.text = String.format("$%.2f", ticket.total)
                    }
                } else if (isAdded) {
                    Toast.makeText(requireContext(), "No se pudo obtener la cuenta", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TicketRequest>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun mostrarDialogoConfirmacion() {
        // Usamos el estilo del tema actual para el diálogo
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmar Pago")
        builder.setMessage("¿Estás seguro de que deseas cerrar la cuenta de $clienteActual? Esto liberará la mesa.")

        builder.setPositiveButton("Sí, cobrar") { _, _ ->
            ejecutarAccionCobrar()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun ejecutarAccionCobrar() {
        RetrofitClient.instance.cobrarCuenta(clienteActual).enqueue(object : Callback<Map<String, String>> {
            override fun onResponse(call: Call<Map<String, String>>, response: Response<Map<String, String>>) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "¡Cuenta cobrada!", Toast.LENGTH_SHORT).show()
                        // Regresamos a la pantalla de mesas
                        findNavController().navigate(R.id.action_cuentaFragment_to_mesasFragment)
                    } else {
                        Toast.makeText(requireContext(), "Error al procesar el cobro", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<Map<String, String>>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error de red al cobrar", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}