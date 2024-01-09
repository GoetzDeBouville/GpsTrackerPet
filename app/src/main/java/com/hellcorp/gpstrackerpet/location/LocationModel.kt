package com.hellcorp.gpstrackerpet.location

import android.health.connect.datatypes.units.Velocity
import org.osmdroid.util.Distance
import org.osmdroid.util.GeoPoint

data class LocationModel(
    val velocity: Float,
    val distance: Float,
    val geoPointList: ArrayList<GeoPoint>
)
