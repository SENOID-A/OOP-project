package trip
import trip.Trip
import user.Driver
import vehicle.VehicleStatus
import repository.TripRepository
import repository.VehicleRepository
import repository.DriverRepository
import location.Location

import java.time.Instant

class TripService(
    private val tripRepo: TripRepository,
    private val vehicleRepo: VehicleRepository,
    private val driverRepo: DriverRepository,
) {

    fun scheduleTrip(
        vehicleId: String,
        driverId: String,
        origin: Location,
        destination: Location,
        scheduledStart: Instant,
        cargoWeightKg: Double,
    ): Trip {
        val vehicle = vehicleRepo.findById(vehicleId)
            ?: error("Vehicle not found")
        val driver = driverRepo.findById(driverId)
            ?: error("Driver not found")

        require(vehicle.isAvailable) { "Vehicle not available" }
        require(driver.onDuty == true) { "Driver not on duty" }
        require(cargoWeightKg <= vehicle.capacityKg) { "Cargo exceeds vehicle capacity" }

        val trip = Trip(
            vehicleId = vehicleId,
            driverId = driverId,
            origin = origin,
            destination = destination,
            scheduledStart = scheduledStart,
            cargoWeightKg = cargoWeightKg,
            status = TripStatus.SCHEDULED,
            actualStart = Instant.now()
        )
        return tripRepo.save(trip)
    }

    fun startTrip(tripId: String): Trip {
        val trip = tripRepo.findById(tripId)
            ?: error("Trip not found")
        require(trip.status == TripStatus.SCHEDULED) { "Trip is not scheduled" }

        val vehicle = vehicleRepo.findById(trip.vehicleId)
            ?: error("Vehicle not found")
        require(vehicle.isAvailable) { "Vehicle not available" }

        val updateTrip = trip.copy(status = TripStatus.IN_PROGRESS,
            actualStart = Instant.now())
//        trip.status = TripStatus.IN_PROGRESS
//        trip.actualStart = Instant.now()
        tripRepo.save(trip)

        vehicle.status = VehicleStatus.IN_TRIP
        vehicleRepo.save(vehicle)

        return trip
    }

    fun completeTrip(tripId: String): Trip {
        val trip = tripRepo.findById(tripId)
            ?: error("Trip not found")

        require(trip.status == TripStatus.IN_PROGRESS) { "Trip is not in progress" }

        val updateTrip = trip.copy(status = TripStatus.SCHEDULED,
            actualEnd = Instant.now())
//        trip.status = TripStatus.COMPLETED
//        trip.actualEnd = Instant.now()
        tripRepo.save(trip)

        val vehicle = vehicleRepo.findById(trip.vehicleId)
        if (vehicle != null) {
            vehicle.status = VehicleStatus.IDLE
            vehicleRepo.save(vehicle)
        }
        return trip
    }

    fun cancelTrip(tripId: String): Trip {
        val trip = tripRepo.findById(tripId)
            ?: error("Trip not found")

        require(trip.status == TripStatus.SCHEDULED || trip.status == TripStatus.IN_PROGRESS) {
            "Trip cannot be cancelled"
        }

        if (trip.status == TripStatus.IN_PROGRESS) {
            vehicleRepo.findById(trip.vehicleId)?.let { v ->
                v.status = VehicleStatus.IDLE
                vehicleRepo.save(v)
            }
        }

        val updateTrip = trip.copy(status = TripStatus.IN_PROGRESS)
//        trip.status = TripStatus.CANCELLED
        tripRepo.save(trip)
        return trip
    }

    fun activeTrips(): List<Trip> =
        tripRepo.all().filter { it.status == TripStatus.IN_PROGRESS }
}