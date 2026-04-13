package com.example.tomapedido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tomapedido.databinding.FragmentSecondBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    // Gestión de pedidos
    private val pedidosPorPersona = mutableMapOf<String, MutableList<Producto>>()
    private val listaNombresPersonas = mutableListOf("Persona 1", "Persona 2", "+ Añadir Persona")

    private var clienteActual: String = "Cliente Nuevo"
    private var personaSeleccionada: String = "Persona 1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Recuperar datos del argumento
        clienteActual = arguments?.getString("nombreCliente") ?: "Cliente Nuevo"

        // 2. Configurar Interfaz
        configurarSpinner()

        val recyclerView = binding.rvMenu
        recyclerView.layoutManager = GridLayoutManager(context, 2) // Mantener diseño en cuadrícula

        // 3. Cargar Menú desde Servidor
        cargarMenu(recyclerView)

        // 4. Configurar botón de envío con el nuevo diseño
        binding.btnEnviarCocina.setOnClickListener {
            if (pedidosPorPersona.isNotEmpty()) {
                mostrarDialogoConfirmacion()
            } else {
                Toast.makeText(context, "Por favor, agrega productos primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarMenu(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        RetrofitClient.instance.getMenu().enqueue(object : Callback<List<Producto>> {
            override fun onResponse(call: Call<List<Producto>>, response: Response<List<Producto>>) {
                if (isAdded && response.isSuccessful) {
                    val listaProductos = response.body() ?: emptyList()
                    recyclerView.adapter = MenuAdapter(listaProductos) { producto ->
                        mostrarDialogoCantidad(producto)
                    }
                }
            }
            override fun onFailure(call: Call<List<Producto>>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(context, "Error de red al cargar el menú", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // --- FLUJO DE PEDIDO: CANTIDAD -> INGREDIENTES ---

    private fun mostrarDialogoCantidad(producto: Producto) {
        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.setText("1")
        input.setSelection(input.text.length)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cantidad: ${producto.nombre}")
            .setMessage("¿Cuántas unidades desea?")
            .setView(input)
            .setPositiveButton("Siguiente", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val texto = input.text.toString()
            if (texto.isNotEmpty()) {
                val cant = texto.toIntOrNull() ?: 1
                dialog.dismiss()
                mostrarDialogoIngredientes(producto, cant)
            } else {
                input.error = "Campo obligatorio"
            }
        }
    }

    private fun mostrarDialogoIngredientes(producto: Producto, cantidadElegida: Int) {
        if (producto.ingredientes.isNullOrEmpty()) {
            val p = producto.copy(cantidad = cantidadElegida)
            agregarProductoAPersona(p)
            return
        }

        val opciones = producto.ingredientes.toTypedArray()
        val seleccionados = BooleanArray(opciones.size) { false }

        AlertDialog.Builder(requireContext())
            .setTitle("Extras / Notas")
            .setMultiChoiceItems(opciones, seleccionados) { _, index, isChecked ->
                seleccionados[index] = isChecked
            }
            .setPositiveButton("Agregar al Pedido") { _, _ ->
                val listaFinal = mutableListOf<String>()
                for (i in opciones.indices) {
                    if (seleccionados[i]) listaFinal.add(opciones[i])
                }

                val productoFinal = producto.copy(
                    cantidad = cantidadElegida,
                    ingredientes_elegidos = listaFinal
                )
                agregarProductoAPersona(productoFinal)
            }
            .setNegativeButton("Atrás") { _, _ -> mostrarDialogoCantidad(producto) }
            .show()
    }

    // --- GESTIÓN DE COMENSALES ---

    private fun configurarSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaNombresPersonas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerComensales.adapter = adapter

        binding.spinnerComensales.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val seleccion = listaNombresPersonas[position]
                if (seleccion == "+ Añadir Persona") {
                    mostrarDialogoNuevaPersona(adapter)
                } else {
                    personaSeleccionada = seleccion
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun mostrarDialogoNuevaPersona(adapter: ArrayAdapter<String>) {
        val input = EditText(requireContext())
        input.hint = "Ej. Persona 3 o Nombre"

        AlertDialog.Builder(requireContext())
            .setTitle("Nuevo Comensal")
            .setView(input)
            .setCancelable(false)
            .setPositiveButton("Crear") { _, _ ->
                val nombre = input.text.toString().trim()
                val nombreFinal = if (nombre.isEmpty()) "Persona ${listaNombresPersonas.size}" else nombre
                val indiceInsertar = listaNombresPersonas.size - 1
                listaNombresPersonas.add(indiceInsertar, nombreFinal)
                adapter.notifyDataSetChanged()
                binding.spinnerComensales.setSelection(indiceInsertar)
                personaSeleccionada = nombreFinal
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                binding.spinnerComensales.setSelection(0)
                dialog.dismiss()
            }
            .show()
    }

    private fun agregarProductoAPersona(producto: Producto) {
        val cantFinal = if (producto.cantidad <= 0) 1 else producto.cantidad

        if (!pedidosPorPersona.containsKey(personaSeleccionada)) {
            pedidosPorPersona[personaSeleccionada] = mutableListOf()
        }

        pedidosPorPersona[personaSeleccionada]?.add(producto.copy(cantidad = cantFinal))
        Toast.makeText(context, "${cantFinal}x ${producto.nombre} asignado a $personaSeleccionada", Toast.LENGTH_SHORT).show()
    }

    // --- ENVÍO FINAL ---

    private fun mostrarDialogoConfirmacion() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmar Pedido")
            .setMessage("¿Deseas enviar los pedidos de ${pedidosPorPersona.size} personas a la cocina?")
            .setPositiveButton("Enviar Ahora") { _, _ -> enviarPedidoAlServidor() }
            .setNegativeButton("Revisar", null)
            .show()
    }

    private fun enviarPedidoAlServidor() {
        val listaDePlatos = pedidosPorPersona.map { (nombre, productos) ->
            Plato(nombre_plato = nombre, items = productos)
        }

        val totalFinal = listaDePlatos.sumOf { plato ->
            plato.items.sumOf { it.precio * it.cantidad }
        }

        val pedido = TicketRequest(
            cliente = clienteActual,
            platos = listaDePlatos,
            total = totalFinal,
            status = "pendiente",
            status_cocina = "pendiente",
            status_mesero = "pendiente",
            timestamp = System.currentTimeMillis()
        )

        RetrofitClient.instance.crearPedido(pedido).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (isAdded && response.isSuccessful) {
                    Toast.makeText(requireContext(), "¡Pedido enviado con éxito!", Toast.LENGTH_LONG).show()
                    pedidosPorPersona.clear()
                    // Opcional: Volver a la lista de mesas
                    // findNavController().navigate(R.id.action_SecondFragment_to_mesasFragment)
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error al conectar con cocina: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}