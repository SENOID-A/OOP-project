package repository

import maintenance.MaintenanceRecord

class MaintenanceRepository {
    private val records = mutableMapOf<String, MaintenanceRecord>()

    fun save(r: MaintenanceRecord): MaintenanceRecord {
        records[r.id] = r
        return r
    }

    fun findById(id: String): MaintenanceRecord? = records[id]

    fun all(): List<MaintenanceRecord> = records.values.toList()

    fun forVehicle(vehicleId: String): List<MaintenanceRecord> =
        records.values.filter { it.vehicleId == vehicleId }
}