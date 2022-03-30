package com.example.test2.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.test2.MainActivity
import com.example.test2.databinding.FragmentSettingsBinding
import com.example.test2.model.entities.SettingsData
import com.example.test2.services.FirebaseService
import com.example.test2.ui.entities.entitiesForm.EntitiesFormActivity


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var fontColorSpinner: Spinner? = null
    private var fontSizeSpinner: Spinner? = null

    private val fontSizeKeys = arrayOf("small", "medium", "large")
    private val fontColorKeys = arrayOf("black", "red", "green")

    private val fontSizeOptions = arrayOf("Small", "Medium", "Large")
    private val fontColorOptions = arrayOf("Black", "Red", "Green")

    private var isInitialFontSizeSet = false
    private var isInitialFontColorSet = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val fontSizeArrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fontSizeOptions)

        val fontColorArrayAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, fontColorOptions)

        binding.fontSizeSpinner.adapter = fontSizeArrayAdapter
        binding.fontColorSpinner.adapter = fontColorArrayAdapter

        binding.fontSizeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isInitialFontSizeSet)
                    setSettings("fontSize", position)

                isInitialFontSizeSet = false
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        binding.fontColorSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isInitialFontColorSet)
                    setSettings("fontColor", position)

                isInitialFontColorSet = false
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        fontColorSpinner = binding.fontColorSpinner
        fontSizeSpinner = binding.fontSizeSpinner

        setInitialData(MainActivity.settings)

        return root
    }

    fun setSettings(option: String, position: Int) {
        val settings: SettingsData = createSettings(option, position)
        FirebaseService.context = context
        FirebaseService.setSettings(settings) {
            MainActivity.isSettingsDownloaded = false
            EntitiesFormActivity.isSettingsDownloaded = false
            Toast.makeText(FirebaseService.context, "Settings saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun createSettings(option: String, position: Int) : SettingsData {
        val settings = SettingsData("", "")

        when {
            option == "fontSize" -> settings.fontSize = fontSizeKeys[position]
            option == "fontColor" -> settings.fontColor = fontColorKeys[position]
        }

        return settings
    }

    fun setInitialData(settings: SettingsData) {
        isInitialFontSizeSet = true
        isInitialFontColorSet = true
        fontSizeSpinner?.setSelection(fontSizeKeys.indexOf(settings.fontSize))
        fontColorSpinner?.setSelection(fontColorKeys.indexOf(settings.fontColor))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}