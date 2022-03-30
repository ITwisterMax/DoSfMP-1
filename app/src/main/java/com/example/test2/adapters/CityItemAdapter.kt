package com.example.test2.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.net.toUri
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.R
import com.example.test2.databinding.CityItemLayoutBinding
import com.example.test2.model.entities.CityItem
import com.example.test2.ui.map.MapFragment
import com.squareup.picasso.Picasso


class CityItemAdapter(private val context: Context?, private var cityItemList:MutableList<CityItem>)
    : RecyclerView.Adapter<CityItemAdapter.CityItemViewHolder>(), Filterable {

    var tempCityItemList = cityItemList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityItemViewHolder {
        val binding = CityItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false)
        return CityItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CityItemViewHolder, position: Int) {
        val cityItem = tempCityItemList[position]
        holder.bind(cityItem)
        holder.itemView.setOnClickListener {
            MapFragment.cityItem = cityItem
            MapFragment.fromEntities = true
            findNavController(it).navigate(R.id.action_entities_to_map)
        }
    }

    override fun getItemCount(): Int {
        return tempCityItemList.size
    }

    class CityItemViewHolder(cityItemLayoutBinding: com.example.test2.databinding.CityItemLayoutBinding)
        : RecyclerView.ViewHolder(cityItemLayoutBinding.root){

        private val binding = cityItemLayoutBinding

        fun bind(cityItem: CityItem){
            binding.cityItemName.text = "${cityItem.cityName}, ${cityItem.country}"
            binding.cityItemLatitude.text = "Latitude: ${"%.2f".format(cityItem.mapPoint?.latitude)}°"
            binding.cityItemLongitude.text = "Longitude: ${"%.2f".format(cityItem.mapPoint?.longitude)}°"
            binding.cityItemWeather.text = cityItem.weatherItem.toString()

            val photoUri = cityItem.image?.downloadUrl
            if (photoUri != null) {
                Picasso.get()
                    .load(photoUri.toUri())
                    .resize(120, 120)
                    .placeholder(R.drawable.ic_baseline_image_not_supported_24)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .into(binding.cityItemImage)
            }
        }
    }

    override fun getFilter(): Filter {
        return object:Filter() {
            override fun performFiltering(searchInfo: CharSequence?): FilterResults {
                tempCityItemList = cityItemList
                var filterList = ArrayList<CityItem>()

                if (searchInfo.isNullOrEmpty() || searchInfo.length < 3) {
                    filterList.clear()
                    filterList.addAll(tempCityItemList)
                }
                else {
                    tempCityItemList.forEach { cityItem ->
                        if (cityItem.cityName.lowercase().startsWith(searchInfo.toString(), true)) {
                            filterList.add(cityItem)
                        }
                    }
                }

                val filterResults = FilterResults()
                filterResults.values = filterList

                return filterResults
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, result: FilterResults?) {
                tempCityItemList = result!!.values as ArrayList<CityItem>
                notifyDataSetChanged()
            }

        }
    }
}