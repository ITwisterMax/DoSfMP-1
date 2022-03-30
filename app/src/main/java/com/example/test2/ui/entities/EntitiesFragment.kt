package com.example.test2.ui.entities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.test2.R
import com.example.test2.adapters.CityItemAdapter
import com.example.test2.databinding.FragmentEntitiesBinding
import com.example.test2.model.entities.CityItem
import com.example.test2.model.entities.WeatherItem
import com.example.test2.services.FirebaseService
import com.example.test2.services.extensions.startAnimation
import com.example.test2.services.extensions.stopAnimation
import com.squareup.okhttp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException

class EntitiesFragment : Fragment() {
    private lateinit var adapter: CityItemAdapter
    private var _binding: FragmentEntitiesBinding? = null
    private lateinit var progressBar: ProgressBar
    private lateinit var addCityButton: Button

    private val API = "1d787d982784cc402d387b4d048d56f9"

    companion object {
        private var cityItemsList: MutableList<CityItem> = ArrayList()
        var isPopulated: Boolean = false

        fun getCityItemsList() : MutableList<CityItem> {
            return cityItemsList
        }
    }

    fun populateList() {
        cityItemsList.clear()
        val cities : MutableList<CityItem> = ArrayList()

        FirebaseService.getCities(cities) { isLast ->
            if (isLast) {
                cityItemsList.clear()
                cities.sortBy { it.cityName }
                cityItemsList.addAll(cities)
                getCitiesWeather()
                onStart()
            }
        }

        isPopulated = true
    }

    fun setUpAdapter() {
        adapter = CityItemAdapter(context, cityItemsList)
        binding.cityItems.adapter = adapter
        binding.cityItems.layoutManager = LinearLayoutManager(context)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_entities, container, false)
        progressBar = view.findViewById(R.id.fragment_entities_progress_bar)
        _binding = FragmentEntitiesBinding.inflate(inflater, container, false)

        populateList()
        setUpAdapter()

        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addCityButton = view.findViewById(R.id.entities_add_city)
        addCityButton.setOnClickListener {
            findNavController().navigate(R.id.action_entities_to_add_city)
        }
    }

    override fun onResume() {
        super.onResume()
        onStart()
    }

    fun getCitiesWeather() {
        for (city in cityItemsList) {
            fetchWeather(city)
        }
    }

    fun fetchWeather(cityItem: CityItem) {
        val coords = cityItem.mapPoint

        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${coords?.latitude}&lon=${coords?.longitude}&units=metric&appid=$API"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(request: Request?, e: IOException?) {
                e?.printStackTrace()
            }

            override fun onResponse(response: Response?) {
                val body = response?.body()?.string()

                if (response?.isSuccessful!!) {
                    val temperature = JSONObject(body!!).getJSONObject("main").getString("temp")
                    cityItem.weatherItem = WeatherItem("", temperature.toFloat(), "°С")
                }
                else {
                    cityItem.weatherItem = WeatherItem("", null, "Err")
                }

                MainScope().launch {
                    withContext(Dispatchers.Default) {}
                    onStart()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.entities_search_view, menu)
        val menuItem = menu.findItem(R.id.entities_search_view)
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onQueryTextChange(searchData: String?): Boolean {
                adapter.filter.filter(searchData)
                adapter.notifyDataSetChanged()

                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }
}