package com.wardrobe.armoire.ui.authentication

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentLoginBinding
import com.wardrobe.armoire.utils.HashUtil
import kotlinx.coroutines.launch
import kotlin.math.log


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnLogin.setOnClickListener {
                val email = textinputUsername.toString()
                val password = passwordInput.toString()

                viewLifecycleOwner.lifecycleScope.launch {
//                    val result = login(email, password)
                        val result = true
                    if (result){
                        Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Login Failed!, Please Check your Email/Password", Toast.LENGTH_SHORT).show()
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
//
//    private suspend fun login(username: String, password: String): Boolean {
//        val hashedPassword = HashUtil.hash(password)
//        val loginResult = userDao.authenticateUser(username, hashedPassword)
//
//        if (loginResult != null) {
//            return true;
//        } else {
//            return false
//        }
//    }
}