package com.example.test2.ui.services.googleMapsLocationPicker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.test2.R
import com.google.android.gms.maps.model.LatLng

class GoogleMapsLocationPickerActivity : AppCompatActivity() {
    companion object {
        var lastPickedLocation: LatLng? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_maps_location_picker)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.hint_pick_location)
    }
}