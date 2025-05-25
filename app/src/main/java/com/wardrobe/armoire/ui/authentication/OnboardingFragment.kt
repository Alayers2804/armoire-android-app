package com.wardrobe.armoire.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentOnboardingBinding
import com.wardrobe.armoire.databinding.FragmentRegisterBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            btnLogin.setOnClickListener{
                findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
            }

            btnRegister.setOnClickListener{
                findNavController().navigate(R.id.action_onboardingFragment_to_registerFragment)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}