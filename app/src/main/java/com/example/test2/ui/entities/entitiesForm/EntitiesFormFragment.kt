package com.example.test2.ui.entities.entitiesForm

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.example.test2.R
import com.example.test2.model.entities.CityItem
import com.example.test2.model.entities.FileData
import com.example.test2.model.entities.MapPoint
import com.example.test2.services.FirebaseService
import com.example.test2.services.extensions.startAnimation
import com.example.test2.services.extensions.stopAnimation
import com.example.test2.ui.entities.EntitiesFragment
import com.example.test2.ui.services.googleMapsLocationPicker.GoogleMapsLocationPickerActivity
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [EntitiesFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EntitiesFormFragment : Fragment() {
    private lateinit var imageImageView: ImageView
    private lateinit var selectImage: Button
    private lateinit var deleteImage: Button
    private var photoUri: Uri? = null

    private lateinit var countryEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText

    private lateinit var pickLocationButton: Button
    private lateinit var deleteLocationButton: Button

    private var selectedReleasePosition: LatLng? = null

    private lateinit var progressBar: ProgressBar

    override fun onResume() {
        super.onResume()
        syncRelease()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_entities_form, container, false)
        initViewObjects(view)
        setupViewObjects()
        return view
    }

    private fun initViewObjects(view: View) {
        imageImageView = view.findViewById(R.id.fragment_entities_form_image)
        selectImage = view.findViewById(R.id.fragment_form_button_select_image)
        deleteImage = view.findViewById(R.id.fragment_form_button_delete_image)

        countryEditText = view.findViewById(R.id.fragment_entities_form_country)
        cityEditText = view.findViewById(R.id.fragment_entities_form_city)
        latitudeEditText = view.findViewById(R.id.fragment_entities_form_latitude)
        longitudeEditText = view.findViewById(R.id.fragment_entities_form_longitude)

        pickLocationButton = view.findViewById(R.id.fragment_entities_form_button_pick_location)
        deleteLocationButton = view.findViewById(R.id.fragment_entities_form_button_delete_location)

        progressBar = view.findViewById(R.id.fragment_form_progress_bar)
    }

    private fun setupViewObjects() {
        setupButtonListeners()
        progressBar.visibility = View.INVISIBLE
    }

    private fun setupButtonListeners() {
        selectImage.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            try {
                i.putExtra("return-data", true)
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 0)
            } catch (ex: ActivityNotFoundException) {
                ex.printStackTrace()
            }
        }

        deleteImage.setOnClickListener {
            photoUri = null
            syncPhoto()
        }

        pickLocationButton.setOnClickListener {
            findNavController().navigate(R.id.action_formFragment_to_googleMapsLocationPickerActivity)
        }

        deleteLocationButton.setOnClickListener {
            latitudeEditText.text.clear()
            longitudeEditText.text.clear()
            selectedReleasePosition = null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_form_confirm, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                requireActivity().onBackPressed()
                true
            }
            R.id.menu_form_confirm -> {
                if (validateInput()) {
                    val release = MapPoint(
                        latitudeEditText.text.toString().toFloat(),
                        longitudeEditText.text.toString().toFloat()
                    )

                    val city = CityItem(
                        id = "",
                        image = FileData("", photoUri.toString()),
                        country = countryEditText.text.toString(),
                        cityName = cityEditText.text.toString(),
                        mapPoint = release,
                        weatherItem = null,
                    )

                    startAnimation(progressBar)

                    FirebaseService.context = context
                    FirebaseService.updateRemoteAsset(city) { updatedAsset, error ->
                        stopAnimation(progressBar)
                        if (error == null) {
                            updateLocalAsset(city)
                            requireActivity().onBackPressed()
                        }
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun validateInput(): Boolean {
        var ret = true
        val country = countryEditText.text.toString()
        val cityName = cityEditText.text.toString()
        val latitude = latitudeEditText.text.toString()
        val longitude = longitudeEditText.text.toString()

        if (country.isEmpty()) {
            countryEditText.error = requireContext().getString(R.string.must_be_not_empty)
            ret = false
        }
        if (cityName.isEmpty()) {
            cityEditText.error = requireContext().getString(R.string.must_be_not_empty)
            ret = false
        }
        if (latitude.isEmpty()) {
            latitudeEditText.error = requireContext().getString(R.string.must_be_not_empty)
            ret = false
        }
        if (longitude.isEmpty()) {
            longitudeEditText.error = requireContext().getString(R.string.must_be_not_empty)
            ret = false
        }

        return ret
    }

    private fun updateLocalAsset(asset: CityItem) {
        EntitiesFragment.getCityItemsList().add(asset)
        EntitiesFragment.getCityItemsList().sortBy { it.cityName }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            try {
                if (data != null) {
                    photoUri = data.data
                    syncPhoto()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun syncPhoto() {
        if (photoUri != null) {
            Picasso.get()
                .load(photoUri)
                .resize(120, 120)
                .placeholder(R.drawable.ic_baseline_image_not_supported_24)
                .error(R.drawable.ic_baseline_image_not_supported_24)
                .into(imageImageView)
            imageImageView.visibility = View.VISIBLE
        } else {
            Picasso.get()
                .load(R.drawable.ic_baseline_image_not_supported_24)
                .resize(120, 120)
                .placeholder(R.drawable.ic_baseline_image_not_supported_24)
                .error(R.drawable.ic_baseline_image_not_supported_24)
                .into(imageImageView)
            imageImageView.visibility = View.VISIBLE
        }
    }

    private fun syncRelease() {
        val newPos = GoogleMapsLocationPickerActivity.lastPickedLocation
        if (newPos != null) {
            selectedReleasePosition = GoogleMapsLocationPickerActivity.lastPickedLocation
            GoogleMapsLocationPickerActivity.lastPickedLocation = null
        }

        val pos = selectedReleasePosition
        if (pos != null) {
            latitudeEditText.setText(pos.latitude.toString())
            longitudeEditText.setText(pos.longitude.toString())
        }
    }
}