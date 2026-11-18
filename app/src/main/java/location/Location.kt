package location

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class Location(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude in -90.0..90.0) { "Latitude out of range: $latitude" }
        require(longitude in -180.0..180.0) { "Longitude out of range: $longitude" }
    }

    /** Great-circle (Haversine) distance in km — NOT road distance. */
    fun distanceTo(other: Location): Double {
        val r = 6371.0
        val dLat = Math.toRadians(other.latitude - latitude)
        val dLon = Math.toRadians(other.longitude - longitude)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(latitude)) *
                cos(Math.toRadians(other.latitude)) *
                sin(dLon / 2).pow(2)
        return 2 * r * atan2(sqrt(a), sqrt(1 - a))
    }

    fun isWithin(radiusKm: Double, other: Location): Boolean =
        distanceTo(other) <= radiusKm

    override fun toString(): String = "%.5f,%.5f".format(latitude, longitude)
}