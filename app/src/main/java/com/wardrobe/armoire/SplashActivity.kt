package com.wardrobe.armoire

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.wardrobe.armoire.ui.authentication.AuthenticationActivity
import com.wardrobe.armoire.utils.DecodeToken
import com.wardrobe.armoire.utils.Preferences
import com.wardrobe.armoire.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = Preferences.getInstance(dataStore)

        enableEdgeToEdge()

        lifecycleScope.launch {
            val token = preferences.getUserToken().first()  // Flow<String> -> String
            val decoded = DecodeToken.decodeToken(token)

            val intent = if (decoded != null && !decoded.isExpired) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, AuthenticationActivity::class.java)
            }

            startActivity(intent)
            finish()
        }
    }
}
