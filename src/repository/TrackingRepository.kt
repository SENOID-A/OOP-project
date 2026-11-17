package repository

import tracking.LocationPing

class TrackingRepository {
    private val pings = mutableListOf<LocationPing>()

    fun save(p: LocationPing): LocationPing {
        pings.add(p)
        return p
    }

    fun forVehicle(vehicleId: String): List<LocationPing> =
        pings.filter { it.vehicleId == vehicleId }

    fun latestForVehicle(vehicleId: String): LocationPing? =
        pings.lastOrNull { it.vehicleId == vehicleId }
}