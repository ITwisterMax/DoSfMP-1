package com.example.test2.model.entities

import com.google.type.LatLng

data class MapPoint(
    val latitude: Float,
    val longitude: Float
    ) {
        constructor() : this(0f, 0f)

        override fun toString(): String {
            return "(${latitude.toString()}, ${longitude.toString()})"
        }
}