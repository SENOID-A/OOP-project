package maintenance

import repository.MaintenanceRepository
import repository.VehicleRepository
import vehicle.VehicleStatus
import java.time.Instant

class MaintenanceService(
    private val maintenanceRepo: MaintenanceRepository,
    private val vehicleRepo: VehicleRepository,
) {

    fun scheduleMaintenance(
        vehicleId: String,
        description: String,
        scheduledDate: Instant,
    ): MaintenanceRecord {
        // 1. FIND
        val vehicle = vehicleRepo.findById(vehicleId)
            ?: error("Vehicle not found")

        // 2. CHECK — only idle vehicles can be scheduled for maintenance
        require(vehicle.status == VehicleStatus.IDLE) {
            "Vehicle must be idle to schedule maintenance"
        }

        // 3. CREATE
        val record = MaintenanceRecord(
            vehicleId = vehicleId,
            description = description,
            scheduledDate = scheduledDate,
        )

        // 4. SAVE
        return maintenanceRepo.save(record)
    }

    fun startMaintenance(recordId: String): MaintenanceRecord {
        // 1. FIND
        val record = maintenanceRepo.findById(recordId)
            ?: error("Maintenance record not found")
        val vehicle = vehicleRepo.findById(record.vehicleId)
            ?: error("Vehicle not found")

        // 2. CHECK
        require(record.status == MaintenanceStatus.SCHEDULED) {
            "Maintenance is not scheduled"
        }

        // 3. CHANGE — both together
        record.status = MaintenanceStatus.IN_PROGRESS
        record.startedAt = Instant.now()
        vehicle.status = VehicleStatus.MAINTENANCE

        // 4. SAVE
        maintenanceRepo.save(record)
        vehicleRepo.save(vehicle)
        return record
    }

    fun completeMaintenance(recordId: String, cost: Double): MaintenanceRecord {
        // 1. FIND
        val record = maintenanceRepo.findById(recordId)
            ?: error("Maintenance record not found")
        val vehicle = vehicleRepo.findById(record.vehicleId)
            ?: error("Vehicle not found")

        // 2. CHECK
        require(record.status == MaintenanceStatus.IN_PROGRESS) {
            "Maintenance is not in progress"
        }
        require(cost >= 0) { "Cost cannot be negative" }

        // 3. CHANGE — both together
        record.status = MaintenanceStatus.COMPLETED
        record.completedAt = Instant.now()
        record.cost = cost
        vehicle.status = VehicleStatus.IDLE

        // 4. SAVE
        maintenanceRepo.save(record)
        vehicleRepo.save(vehicle)
        return record
    }

    fun cancelMaintenance(recordId: String): MaintenanceRecord {
    // 1. FIND
    val record = maintenanceRepo.findById(recordId)
        ?: error("Maintenance record not found")

    // 2. CHECK
    require(record.status == MaintenanceStatus.SCHEDULED || record.status == MaintenanceStatus.IN_PROGRESS) {
        "Maintenance cannot be cancelled"
    }

    // 3. CHANGE
    val wasStarted = record.status == MaintenanceStatus.IN_PROGRESS
    record.status = MaintenanceStatus.CANCELLED
    record.completedAt = Instant.now()

    // 4. SAVE
    maintenanceRepo.save(record)
    if (wasStarted) {
        vehicleRepo.findById(record.vehicleId)?.let {
            it.status = VehicleStatus.IDLE
            vehicleRepo.save(it)
        }
    }
    return record


    fun activeMaintenance(): List<MaintenanceRecord> =
        maintenanceRepo.all().filter { it.status == MaintenanceStatus.IN_PROGRESS }
}

