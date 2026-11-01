package trip

import java.time.Instant
import java.util.UUID

enum class TripStatus { SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED }

data class Trip(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val driverId: String,
    val origin: Location,
    val destination: Location,
    val scheduledStart: Instant,
    val cargoWeightKg: Double,
    val status: TripStatus = TripStatus.SCHEDULED,
    val actualStart: Instant? = null,
    val actualEnd: Instant? = null,
    val plannedDistanceKm: Double = origin.distanceTo(destination),
    val actualDistanceKm: Double = 0.0,
    val cancellationReason: String? = null,
) {
    val isActive: Boolean
        get() = status == TripStatus.IN_PROGRESS

    val isFinished: Boolean
        get() = status == TripStatus.COMPLETED || status == TripStatus.CANCELLED

    val durationSeconds: Long?
        get() = if (actualStart != null && actualEnd != null)
            java.time.Duration.between(actualStart, actualEnd).seconds
        else null
}