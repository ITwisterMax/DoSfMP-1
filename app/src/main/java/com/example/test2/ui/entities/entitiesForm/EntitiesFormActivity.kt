package com.example.test2.ui.entities.entitiesForm

import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.test2.MainActivity
import com.example.test2.R
import com.example.test2.model.entities.CityItem
import com.example.test2.model.entities.SettingsData
import com.example.test2.model.entities.WeatherItem
import com.example.test2.services.FirebaseService
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EntitiesFormActivity : AppCompatActivity() {

    companion object {
        var instance : EntitiesFormActivity? = null
        var settings : SettingsData = SettingsData("", "")
        var isSettingsDownloaded : Boolean = false
        var theme : Resources.Theme? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (instance == null) {
            instance = this
        }

        if (!isSettingsDownloaded) {
            FirebaseService.getSettings(settings) { isSuccess ->
                if (settings.fontColor == "black" && settings.fontSize == "small")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_SmallBlack, true)
                else if (settings.fontColor == "black" && settings.fontSize == "medium")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2, true)
                else if (settings.fontColor == "black" && settings.fontSize == "large")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_LargeBlack, true)
                else if (settings.fontColor == "red" && settings.fontSize == "small")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_SmallRed, true)
                else if (settings.fontColor == "red" && settings.fontSize == "medium")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_MediumRed, true)
                else if (settings.fontColor == "red" && settings.fontSize == "large")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_LargeRed, true)
                else if (settings.fontColor == "green" && settings.fontSize == "small")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_SmallGreen, true)
                else if (settings.fontColor == "green" && settings.fontSize == "medium")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_MediumGreen, true)
                else if (settings.fontColor == "green" && settings.fontSize == "large")
                    EntitiesFormActivity.theme?.applyStyle(R.style.Theme_Test2_LargeGreen, true)

                isSettingsDownloaded = true
                instance?.recreate()
            }

            isSettingsDownloaded = true
        }

        setContentView(R.layout.activity_entities_form)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.cities)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun getTheme(): Resources.Theme? {
        val theme = super.getTheme()

        if (EntitiesFormActivity.theme == null)
            EntitiesFormActivity.theme = theme

        return EntitiesFormActivity.theme
    }
}