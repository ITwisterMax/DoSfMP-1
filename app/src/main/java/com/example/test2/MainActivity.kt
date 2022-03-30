package com.example.test2

import android.content.res.Resources.Theme
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test2.databinding.ActivityMainBinding
import com.example.test2.model.entities.SettingsData
import com.example.test2.services.FirebaseService
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        var instance : MainActivity? = null
        var settings : SettingsData = SettingsData("", "")
        var isSettingsDownloaded : Boolean = false
        var theme : Theme? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (instance == null) {
            instance = this
        }

        if (!isSettingsDownloaded) {
            FirebaseService.getSettings(settings) { isSuccess ->
                if (settings.fontColor == "black" && settings.fontSize == "small") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_SmallBlack, true)
                }
                else if (settings.fontColor == "black" && settings.fontSize == "medium") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2, true)
                }
                else if (settings.fontColor == "black" && settings.fontSize == "large") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_LargeBlack, true)
                }
                else if (settings.fontColor == "red" && settings.fontSize == "small") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_SmallRed, true)
                }
                else if (settings.fontColor == "red" && settings.fontSize == "medium") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_MediumRed, true)
                }
                else if (settings.fontColor == "red" && settings.fontSize == "large") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_LargeRed, true)
                }
                else if (settings.fontColor == "green" && settings.fontSize == "small") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_SmallGreen, true)
                }
                else if (settings.fontColor == "green" && settings.fontSize == "medium") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_MediumGreen, true)
                }
                else if (settings.fontColor == "green" && settings.fontSize == "large") {
                    MainActivity.theme?.applyStyle(R.style.Theme_Test2_LargeGreen, true)
                }

                isSettingsDownloaded = true
                instance?.recreate()
            }

            isSettingsDownloaded = true
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_entities, R.id.navigation_map, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun getTheme(): Theme? {
        val theme = super.getTheme()

        if (MainActivity.theme == null)
            MainActivity.theme = theme

        return MainActivity.theme
    }
}