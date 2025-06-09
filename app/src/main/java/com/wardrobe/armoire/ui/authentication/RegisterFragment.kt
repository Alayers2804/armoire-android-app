package com.wardrobe.armoire.ui.authentication

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentRegisterBinding
import com.wardrobe.armoire.model.user.Gender
import com.wardrobe.armoire.model.user.UserModel
import com.wardrobe.armoire.utils.HashUtil
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import kotlinx.coroutines.launch
import java.util.logging.Logger

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var viewModel: AuthenticationViewmodel? = null

    private enum class RegistrationStep {
        BASIC_INFO, GENDER, STYLE
    }

    private var currentStep = RegistrationStep.BASIC_INFO

    private val selectedStyles = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
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

        var selectedGender: Gender? = null

        with(binding) {
            showStep(currentStep)

            var name: String
            var username: String
            var email: String
            var password: String

            val femaleImage = view.findViewById<ImageView>(R.id.image_female)
            val maleImage = view.findViewById<ImageView>(R.id.image_male)
            val maleStylesLayout = view.findViewById<LinearLayout>(R.id.layout_styles_men)
            val femaleStylesLayout = view.findViewById<LinearLayout>(R.id.layout_styles_women)

            btnRegister.setOnClickListener {
                name = textinputName.editText?.text.toString().trim()
                username = textinputUsername.editText?.text.toString().trim()
                email = textinputEmail.editText?.text.toString().trim()
                password = passwordInput.editText?.text.toString().trim()
                when (currentStep) {
                    RegistrationStep.BASIC_INFO -> {
                        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(context, "Fields cannot be empty", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            currentStep = RegistrationStep.GENDER
                            showStep(currentStep)
                        }
                    }

                    RegistrationStep.GENDER -> {
                        if (selectedGender == null) {
                            Toast.makeText(context, "Please select a gender", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Log.d("What is the gender", selectedGender.toString())
                            currentStep = RegistrationStep.STYLE
                            showStep(currentStep)

                            if (selectedGender == Gender.MAN) {
                                maleStylesLayout.visibility = View.VISIBLE
                                femaleStylesLayout.visibility = View.GONE
                                setupStyleSelection(maleStylesLayout)
                            } else {
                                maleStylesLayout.visibility = View.GONE
                                femaleStylesLayout.visibility = View.VISIBLE
                                setupStyleSelection(femaleStylesLayout)
                            }
                        }
                    }

                    RegistrationStep.STYLE -> {
                        try {
                            viewLifecycleOwner.lifecycleScope.launch {
                                viewModel?.register(
                                    name,
                                    username,
                                    email,
                                    password,
                                    selectedGender!!,
                                    selectedStyles
                                )
                                Log.d("Data Saved", "Data saved successfully : $name, $username, $email, $password, $selectedGender, $selectedStyles")
                                Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT)
                                    .show()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                        } catch (e: Exception) {
                            Log.e("Error inputting data", e.toString())
                        }

                    }
                }
            }
            signInLink.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }

            femaleImage.setOnClickListener {
                selectedGender = Gender.WOMAN
                highlightSelectedGender(femaleImage, maleImage)
            }

            maleImage.setOnClickListener {
                selectedGender = Gender.MAN
                highlightSelectedGender(maleImage, femaleImage)
            }

            setupStyleSelection(binding.layoutStylesMen)
            setupStyleSelection(binding.layoutStylesWomen)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun showStep(step: RegistrationStep) {
        binding.layoutBasicInfo.visibility =
            if (step == RegistrationStep.BASIC_INFO) View.VISIBLE else View.GONE

        binding.layoutTextInfo.visibility =
            if (step == RegistrationStep.BASIC_INFO) View.VISIBLE else View.GONE

        binding.btnRegister.text =
            if (step == RegistrationStep.STYLE) "Sign up" else "Next"

        binding.layoutGender.visibility =
            if (step == RegistrationStep.GENDER) View.VISIBLE else View.GONE

        binding.textViewSignUp.text =
            if (step == RegistrationStep.GENDER) "Select your gender" else if (step == RegistrationStep.STYLE) "Select Your Aesthetic(s)" else "Sign up"

        binding.textViewAestethics.visibility =
            if (step == RegistrationStep.STYLE) View.VISIBLE else View.GONE

        binding.layoutStyle.visibility =
            if (step == RegistrationStep.STYLE) View.VISIBLE else View.GONE
    }

    private fun highlightSelectedGender(selected: ImageView, unselected: ImageView) {

        selected.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
        unselected.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()

        selected.setBackgroundResource(R.drawable.selection_border)
        unselected.setBackgroundResource(0)
    }

    private fun setupStyleSelection(styleLayout: LinearLayout) {
        for (i in 0 until styleLayout.childCount) {
            val child = styleLayout.getChildAt(i)

            if (child is LinearLayout) {
                setupStyleSelection(child)
            } else if (child is ImageView) {
                val style = child.tag?.toString() ?: continue

                child.setOnClickListener {
                    if (selectedStyles.contains(style)) {
                        selectedStyles.remove(style)
                        child.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).start()
                        child.setBackgroundResource(0)
                    } else {
                        selectedStyles.add(style)
                        child.animate().scaleX(1.1f).scaleY(1.1f).setDuration(150).start()
                        child.setBackgroundResource(R.drawable.selection_border)
                    }
                }
            }
        }
    }
}