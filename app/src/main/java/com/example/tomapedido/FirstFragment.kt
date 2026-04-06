package com.example.tomapedido

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.tomapedido.databinding.FragmentFirstBinding
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnLogin = view.findViewById<Button>(R.id.btnLogin)
        val etUser = view.findViewById<EditText>(R.id.etUsername)
        val etPin = view.findViewById<EditText>(R.id.etPin)

        btnLogin.setOnClickListener {
            val user = etUser.text.toString()
            val pin = etPin.text.toString()

            if (user.isNotEmpty() && pin.isNotEmpty()) {
                val loginReq = LoginRequest(user, pin)

                // Llamada a tu servidor Python
                RetrofitClient.instance.login(loginReq).enqueue(object : retrofit2.Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val body = response.body()
                            Toast.makeText(context, "Bienvenido ${body?.nombre}", Toast.LENGTH_SHORT).show()

                            // Si el login es correcto, pasamos a la siguiente pantalla
                            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                        } else {
                            Toast.makeText(context, "Error: Usuario o PIN incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(context, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}