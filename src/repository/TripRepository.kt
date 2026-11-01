package repository
import trip.Trip
import trip.TripStatus


class TripRepository {
    private val trips = mutableMapOf<String, Trip>()

    fun save(t: Trip): Trip {
        trips[t.id] = t
        return t
    }

    fun findById(id: String): Trip? = trips[id]

    fun all(): List<Trip> = trips.values.toList()

    fun activeForVehicle(vehicleId: String): Trip? =
        trips.values.firstOrNull {
            it.vehicleId == vehicleId && it.status == TripStatus.IN_PROGRESS
        }

    fun forVehicle(vehicleId: String): List<Trip> =
        trips.values.filter { it.vehicleId == vehicleId }

    fun forDriver(driverId: String): List<Trip> =
        trips.values.filter { it.driverId == driverId }
}