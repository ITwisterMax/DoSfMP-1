package com.example.test2.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.test2.R
import com.example.test2.model.entities.CityItem
import com.example.test2.ui.entities.EntitiesFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment() {
    private val CAMERA_ZOOM = 5.0f
    private val CAMERA_TILT = 0.0f
    private val CAMERA_BEARING = 0.0f

    private lateinit var mapFragment: SupportMapFragment
    private var mapIsReady = false
    lateinit var googleMapItem: GoogleMap

    companion object {
        var cityItem: CityItem? = null
        var fromEntities: Boolean = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        initViewObjects(view)
        setupViewObjects()

        return view
    }

    private fun initViewObjects(view: View) {
        mapFragment = childFragmentManager.findFragmentById(R.id.fragment_map) as SupportMapFragment
    }

    private fun setupViewObjects() {
        setupButtonListeners()

        mapFragment.getMapAsync { googleMap ->
            googleMapItem = googleMap
            mapIsReady = true
            updateMarkers()

            if (fromEntities) {
                googleMapItem.moveCamera(
                    CameraUpdateFactory.
                    newCameraPosition(
                        CameraPosition(
                            LatLng(
                                cityItem?.mapPoint?.latitude!!.toDouble(),
                                cityItem?.mapPoint?.longitude!!.toDouble()),
                            CAMERA_ZOOM,
                            CAMERA_TILT,
                            CAMERA_BEARING)
                    )
                )

                cityItem = null
                fromEntities = false
            }
        }
    }

    private fun setupButtonListeners() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finishAffinity()
        }
    }

    private fun updateMarkers() {
        val pendingMarkers = EntitiesFragment.getCityItemsList()
        for (cityItem in pendingMarkers) {
            googleMapItem.addMarker(
                MarkerOptions().
                position(LatLng(cityItem.mapPoint?.latitude!!.toDouble(), cityItem?.mapPoint!!.longitude.toDouble())).
                title("${cityItem.cityName}, ${cityItem.country} (${"%.2f".format(cityItem.mapPoint?.latitude)}°; ${"%.2f".format(cityItem.mapPoint?.longitude)}°)").
                snippet(cityItem.weatherItem.toString()))
        }
    }

}
