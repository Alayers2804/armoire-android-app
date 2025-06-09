package com.wardrobe.armoire

import android.content.BroadcastReceiver
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.wardrobe.armoire.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        binding.menuNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.btn_my_wardrobe -> {
                    if (navController.currentDestination?.id != R.id.wardrobeFragment) {
                        navController.navigate(R.id.wardrobeFragment)
                    }
                    true
                }

                R.id.btn_browse -> {
                    navController.navigate(R.id.browseFragment)
                    true
                }

                R.id.btn_shop -> {
                    navController.navigate(R.id.shopFragment)
                    true
                }

                R.id.btn_profile -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }

                else -> false
            }
        }

    }
}