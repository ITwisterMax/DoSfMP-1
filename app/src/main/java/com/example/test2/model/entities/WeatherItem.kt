package com.example.test2.model.entities

data class WeatherItem(
    val id: String,
    var degrees: Float?,
    var measure: String?
    ) {
        constructor() : this("", 0f, "")

        override fun toString(): String {
            if (degrees == null) {
                return measure!!
            }

            return "${degrees.toString()} $measure"
        }
}
