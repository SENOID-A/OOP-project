package trip

import java.time.Instant

/** Result of a trip operation — business rejections are values, not exceptions. */
sealed interface TripResult<out T> {
    data class Ok<T>(val value: T) : TripResult<T>
    data class Rejected(val reason: String) : TripResult<Nothing>
}

class TripService(
    private val tripRepo: TripRepository,
    private val vehicleRepo: VehicleRepository,
    private val driverRepo: DriverRepository,
    private val alertRepo: AlertRepository,
) {

    // ---------- Commands ----------

    fun scheduleTrip(
        vehicleId: String,
        driverId: String,
        origin: Location,
        destination: Location,
        scheduledStart: Instant,
        cargoWeightKg: Double,
    ): TripResult<Trip> {
        val vehicle = vehicleRepo.findById(vehicleId)
            ?: return TripResult.Rejected("Vehicle not found: $vehicleId")
        val driver = driverRepo.findById(driverId)
            ?: return TripResult.Rejected("Driver not found: $driverId")

        if (!vehicle.isAvailable)
            return TripResult.Rejected("Vehicle ${vehicle.plateNumber} is not available (${vehicle.status})")
        if (!driver.onDuty)
            return TripResult.Rejected("Driver ${driver.name} is not on duty")
        if (cargoWeightKg <= 0 || cargoWeightKg > vehicle.capacityKg)
            return TripResult.Rejected(
                "Cargo $cargoWeightKg kg outside allowed range (0, ${vehicle.capacityKg}]"
            )
        if (scheduledStart.isBefore(Instant.now().minusSeconds(60)))
            return TripResult.Rejected("Scheduled start is in the past")

        val trip = Trip(
            vehicleId = vehicleId,
            driverId = driverId,
            origin = origin,
            destination = destination,
            scheduledStart = scheduledStart,
            cargoWeightKg = cargoWeightKg,
        )
        tripRepo.save(trip)

        // Reserve the vehicle — but note: status stays IDLE until startTrip().
        // If you want explicit reservation, add a RESERVED status in VehicleStatus.
        return TripResult.Ok(trip)
    }

    fun startTrip(tripId: String): TripResult<Trip> {
        val trip = tripRepo.findById(tripId)
            ?: return TripResult.Rejected("Trip not found: $tripId")

        if (trip.status != TripStatus.SCHEDULED)
            return TripResult.Rejected("Trip $tripId is not SCHEDULED (was ${trip.status})")

        val vehicle = vehicleRepo.findById(trip.vehicleId)
            ?: return TripResult.Rejected("Vehicle ${trip.vehicleId} no longer exists")

        if (!vehicle.isAvailable)
            return TripResult.Rejected(
                "Vehicle ${vehicle.plateNumber} unavailable at start (${vehicle.status})"
            )

        val started = trip.copy(
            status = TripStatus.IN_PROGRESS,
            actualStart = Instant.now(),
        )
        tripRepo.save(started)
        vehicleRepo.save(vehicle.copy(status = VehicleStatus.IN_TRIP))
        return TripResult.Ok(started)
    }

    fun completeTrip(tripId: String, actualDistanceKm: Double? = null): TripResult<Trip> {
        val trip = tripRepo.findById(tripId)
            ?: return TripResult.Rejected("Trip not found: $tripId")

        if (trip.status != TripStatus.IN_PROGRESS)
            return TripResult.Rejected("Trip $tripId is not IN_PROGRESS (was ${trip.status})")

        val completed = trip.copy(
            status = TripStatus.COMPLETED,
            actualEnd = Instant.now(),
            actualDistanceKm = actualDistanceKm ?: trip.plannedDistanceKm,
        )
        tripRepo.save(completed)

        vehicleRepo.findById(trip.vehicleId)?.let { v ->
            vehicleRepo.save(v.copy(status = VehicleStatus.IDLE))
        }
        return TripResult.Ok(completed)
    }

    fun cancelTrip(tripId: String, reason: String): TripResult<Trip> {
        val trip = tripRepo.findById(tripId)
            ?: return TripResult.Rejected("Trip not found: $tripId")

        if (trip.isFinished)
            return TripResult.Rejected("Trip $tripId already finished (${trip.status})")
        if (reason.isBlank())
            return TripResult.Rejected("Cancellation reason required")

        val cancelled = trip.copy(
            status = TripStatus.CANCELLED,
            cancellationReason = reason,
            actualEnd = Instant.now(),
        )
        tripRepo.save(cancelled)

        // Free the vehicle only if it was actually in a trip for this booking
        val v = vehicleRepo.findById(trip.vehicleId)
        if (v != null && v.status == VehicleStatus.IN_TRIP) {
            vehicleRepo.save(v.copy(status = VehicleStatus.IDLE))
        }
        return TripResult.Ok(cancelled)
    }

    // ---------- Queries ----------

    fun activeTrips(): List<Trip> = tripRepo.all().filter { it.isActive }

    fun tripsForVehicle(vehicleId: String): List<Trip> =
        tripRepo.all().filter { it.vehicleId == vehicleId }

    fun tripsForDriver(driverId: String): List<Trip> =
        tripRepo.all().filter { it.driverId == driverId }
}