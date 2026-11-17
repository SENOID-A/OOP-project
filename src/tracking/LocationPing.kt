package tracking

import location.Location
import java.time.Instant
import java.util.UUID

data class LocationPing(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val location: Location,
    val timestamp: Instant = Instant.now(),
)