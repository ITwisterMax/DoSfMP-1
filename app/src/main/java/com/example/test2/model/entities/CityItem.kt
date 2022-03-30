package com.example.test2.model.entities

import android.media.Image

data class CityItem(
    var id: String,
    var image: FileData?,
    var country : String,
    var cityName: String,
    var mapPoint: MapPoint?,
    var weatherItem: WeatherItem?
    ) {
        constructor() : this("", null, "", "", null, null)
}
