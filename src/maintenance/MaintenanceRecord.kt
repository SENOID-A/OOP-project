package maintenance

import java.time.Instant
import java.util.UUID

enum class MaintenanceStatus { SCHEDULED, IN_PROGRESS, COMPLETED }

data class MaintenanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val vehicleId: String,
    val description: String,
    val scheduledDate: Instant,
    var status: MaintenanceStatus = MaintenanceStatus.SCHEDULED,
    var startedAt: Instant? = null,
    var completedAt: Instant? = null,
    var cost: Double = 0.0,
)