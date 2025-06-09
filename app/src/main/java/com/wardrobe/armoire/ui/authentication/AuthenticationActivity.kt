package com.wardrobe.armoire.ui.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.wardrobe.armoire.BaseViewModelFactory
import com.wardrobe.armoire.MainActivity
import com.wardrobe.armoire.R
import com.wardrobe.armoire.databinding.ActivityAuthenticationBinding
import com.wardrobe.armoire.utils.DecodeToken
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.UserPreferences
import com.wardrobe.armoire.utils.dataStore

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var navController: NavController
    private var mainActivityStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val preferences = Preferences.getInstance(dataStore)

        val factory = BaseViewModelFactory {
            AuthenticationViewmodel(application, preferences)
        }
        val authenticationViewmodel = ViewModelProvider(this, factory)[AuthenticationViewmodel::class.java]

        authenticationViewmodel.getUserPreference(UserPreferences.User_Token)
            .observe(this) { token ->
                val decoded = DecodeToken.decodeToken(token)
                if (!mainActivityStarted && decoded != null && !decoded.isExpired) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("token", token)
                    startActivity(intent)
                    mainActivityStarted = true
                    finish()
                }
            }

        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
    }
}