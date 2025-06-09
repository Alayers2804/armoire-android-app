package com.wardrobe.armoire.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.MainActivity
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentLoginBinding
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthenticationViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = Preferences.getInstance(requireContext().dataStore)

        val factory = BaseViewModelFactory {
            AuthenticationViewmodel(requireActivity().application, preferences)
        }
        val authenticationViewModel =
            ViewModelProvider(this, factory)[AuthenticationViewmodel::class.java]

        this.viewModel = authenticationViewModel

        with(binding) {
            btnLogin.setOnClickListener {
                val email = textinputUsername.editText?.text.toString()
                val password = passwordInput.editText?.text.toString()

                viewLifecycleOwner.lifecycleScope.launch {
                    val result = viewModel.login(email.trim(), password.trim())
                    if (result == true) {
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            context,
                            "Login Failed!, Please Check your Email/Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            signUpLink.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        unregisterReceiver()
        _binding = null
    }

}