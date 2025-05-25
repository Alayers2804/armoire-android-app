package com.wardrobe.armoire.ui.authentication

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.wardrobe.armoire.AppDatabase
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.FragmentRegisterBinding
import com.wardrobe.armoire.model.user.Gender
import com.wardrobe.armoire.model.user.UserModel
import com.wardrobe.armoire.utils.HashUtil
import kotlinx.coroutines.launch

class RegisterFragment: Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var gender: Gender
//
//    private val app = requireActivity().application
//    private val userDao = AppDatabase.getDatabase(app).userDao()

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

        var selectedGender: Gender? = null

        with(binding) {
            showStep(currentStep)
            val name = textinputName.toString()
            val username = textinputUsername.toString()
            val email = textinputEmail.toString()
            val password = passwordInput.toString()
            val femaleImage = view.findViewById<ImageView>(R.id.image_female)
            val maleImage = view.findViewById<ImageView>(R.id.image_male)
            val maleStylesLayout = view.findViewById<LinearLayout>(R.id.layout_styles_men)
            val femaleStylesLayout = view.findViewById<LinearLayout>(R.id.layout_styles_women)

            btnRegister.setOnClickListener {
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
                            currentStep = RegistrationStep.STYLE
                            showStep(currentStep)
                        }
                    }

                    RegistrationStep.STYLE -> {
                        when (gender) {
                            Gender.MAN -> {
                                maleStylesLayout.visibility = View.VISIBLE
                                femaleStylesLayout.visibility = View.GONE
                                setupStyleSelection(maleStylesLayout)
                            }

                            Gender.WOMAN -> {
                                maleStylesLayout.visibility = View.GONE
                                femaleStylesLayout.visibility = View.VISIBLE
                                setupStyleSelection(femaleStylesLayout)
                            }
                        }

                        viewLifecycleOwner.lifecycleScope.launch {
//                            register(name, username, email, password, selectedGender!!, selectedStyles)
                            Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
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
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

//    private suspend fun register(
//        name: String,
//        username: String,
//        email: String,
//        password: String,
//        gender: Gender,
//        style: List<String>?
//    ) {
//
//        val hashedPassword = HashUtil.hash(password)
//
//        val userData = UserModel(
//            name = name,
//            username = username,
//            password = hashedPassword,
//            email = email,
//            gender = gender.toString(),
//            style = style
//        )
//
//        userDao.registerUser(userData)
//    }

    private fun showStep(step: RegistrationStep) {
        binding.layoutBasicInfo.visibility =
            if (step == RegistrationStep.BASIC_INFO) View.VISIBLE else View.GONE
        binding.layoutGender.visibility =
            if (step == RegistrationStep.GENDER) View.VISIBLE else View.GONE
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
            val textView = styleLayout.getChildAt(i) as? TextView ?: continue
            textView.setOnClickListener {
                val style = textView.text.toString()
                if (selectedStyles.contains(style)) {
                    selectedStyles.remove(style)
                    textView.setBackgroundResource(0)
                    textView.setTypeface(null, Typeface.NORMAL)
                } else {
                    selectedStyles.add(style)
                    textView.setBackgroundResource(R.drawable.selection_border) // optional
                    textView.setTypeface(null, Typeface.BOLD)
                }
            }
        }
    }

}