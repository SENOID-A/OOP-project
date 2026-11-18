package tracking

import location.Location
import repository.TrackingRepository
import repository.TripRepository
import repository.VehicleRepository
import vehicle.Vehicle
import vehicle.VehicleStatus

class TrackingService(
    private val trackingRepo: TrackingRepository,
    private val vehicleRepo: VehicleRepository,
    private val tripRepo: TripRepository,
) {

    /** Called whenever a vehicle reports a new GPS position. */
    fun updateLocation(vehicleId: String, newLocation: Location): LocationPing {
        // 1. FIND
        val vehicle = vehicleRepo.findById(vehicleId)
            ?: error("Vehicle not found")

        // 2. CHECK
        require(vehicle.status != VehicleStatus.OFFLINE) { "Vehicle is offline" }

        // 3. CHANGE
        val movedKm = vehicle.currentLocation.distanceTo(newLocation)
        vehicle.currentLocation = newLocation
        vehicle.odometerKm += movedKm

        // 4. SAVE
        vehicleRepo.save(vehicle)

        // Add the distance to the active trip (Trip is immutable, so use copy)
        tripRepo.activeForVehicle(vehicleId)?.let { trip ->
            tripRepo.save(trip.copy(actualDistanceKm = trip.actualDistanceKm + movedKm))
        }

        return trackingRepo.save(LocationPing(vehicleId = vehicleId, location = newLocation))
    }

    fun currentLocation(vehicleId: String): Location =
        vehicleRepo.findById(vehicleId)?.currentLocation
            ?: error("Vehicle not found")

    fun history(vehicleId: String): List<LocationPing> =
        trackingRepo.forVehicle(vehicleId)

    fun distanceToDestinationKm(tripId: String): Double {
        val trip = tripRepo.findById(tripId) ?: error("Trip not found")
        val vehicle = vehicleRepo.findById(trip.vehicleId) ?: error("Vehicle not found")
        return vehicle.currentLocation.distanceTo(trip.destination)
    }

    fun hasArrived(tripId: String, radiusKm: Double = 0.1): Boolean =
        distanceToDestinationKm(tripId) <= radiusKm

    /** Useful for the Warehouse and Distribution Locator link: which vehicles are near a warehouse? */
    fun vehiclesNear(location: Location, radiusKm: Double): List<Vehicle> =
        vehicleRepo.all().filter { it.currentLocation.isWithin(radiusKm, location) }

    fun availableVehiclesNear(location: Location, radiusKm: Double): List<Vehicle> =
        vehicleRepo.available().filter { it.currentLocation.isWithin(radiusKm, location) }
}