package com.example.tomapedido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tomapedido.databinding.FragmentSecondBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMenu)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        // Llamada al servidor para obtener el menú
        RetrofitClient.instance.getMenu().enqueue(object : retrofit2.Callback<List<Producto>> {
            override fun onResponse(call: retrofit2.Call<List<Producto>>, response: retrofit2.Response<List<Producto>>) {
                if (response.isSuccessful) {
                    val listaProductos = response.body() ?: emptyList()
                    // Le pasamos los datos de MongoDB al Adapter
                    recyclerView.adapter = MenuAdapter(listaProductos)
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Producto>>, t: Throwable) {
                android.widget.Toast.makeText(context, "Error al cargar menú: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}