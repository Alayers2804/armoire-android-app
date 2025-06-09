package com.wardrobe.armoire.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.MainActivity
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentLoginBinding
import com.wardrobe.armoire.databinding.FragmentProfileBinding
import com.wardrobe.armoire.ui.authentication.AuthenticationActivity
import com.wardrobe.armoire.ui.authentication.AuthenticationViewmodel
import com.wardrobe.armoire.utils.DecodeToken
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.UserPreferences
import com.wardrobe.armoire.utils.dataStore
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthenticationViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = Preferences.getInstance(requireContext().dataStore)

        val factory = BaseViewModelFactory {
            AuthenticationViewmodel(requireActivity().application, preferences)
        }
        val authenticationViewModel = ViewModelProvider(this, factory)[AuthenticationViewmodel::class.java]

        this.viewModel = authenticationViewModel

        with(binding) {
            viewModel.getUserPreference(UserPreferences.Username).observe(viewLifecycleOwner) { username ->
                txtUsernameProfile.text = username
            }

            viewModel.getUserPreference(UserPreferences.Email).observe(viewLifecycleOwner) { email ->
                txtEmailProfile.text = email
            }

            binding.subscriptionPlan.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_subscriptionFragment)
            }

            binding.yourOrder.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_yourOrderFragment)
            }

            binding.incomingOrder.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_orderFragment)
            }

            btnLogout.setOnClickListener {
                viewModel.clearUserPreferences()
                val intent = Intent(requireContext(), AuthenticationActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

}