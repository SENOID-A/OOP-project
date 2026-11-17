package repository

import database.Database
import maintenance.MaintenanceRecord
import maintenance.MaintenanceStatus
import java.sql.ResultSet
import java.time.Instant

class MaintenanceRepository {

    fun save(r: MaintenanceRecord): MaintenanceRecord {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT OR REPLACE INTO maintenance_records
                (id, vehicle_id, description, scheduled_date, status,
                 started_at, completed_at, cost)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, r.id)
                ps.setString(2, r.vehicleId)
                ps.setString(3, r.description)
                ps.setString(4, r.scheduledDate.toString())
                ps.setString(5, r.status.name)
                ps.setString(6, r.startedAt?.toString())
                ps.setString(7, r.completedAt?.toString())
                ps.setDouble(8, r.cost)
                ps.executeUpdate()
            }
        }
        return r
    }

    fun findById(id: String): MaintenanceRecord? =
        queryList("SELECT * FROM maintenance_records WHERE id = ?", id).firstOrNull()

    fun all(): List<MaintenanceRecord> =
        queryList("SELECT * FROM maintenance_records ORDER BY rowid")

    fun forVehicle(vehicleId: String): List<MaintenanceRecord> =
        queryList(
            "SELECT * FROM maintenance_records WHERE vehicle_id = ? ORDER BY rowid",
            vehicleId
        )

    private fun queryList(sql: String, param: String? = null): List<MaintenanceRecord> =
        Database.connect().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                if (param != null) ps.setString(1, param)
                ps.executeQuery().use { rs ->
                    val list = mutableListOf<MaintenanceRecord>()
                    while (rs.next()) list.add(toRecord(rs))
                    list
                }
            }
        }

    private fun toRecord(rs: ResultSet) = MaintenanceRecord(
        id = rs.getString("id"),
        vehicleId = rs.getString("vehicle_id"),
        description = rs.getString("description"),
        scheduledDate = Instant.parse(rs.getString("scheduled_date")),
        status = MaintenanceStatus.valueOf(rs.getString("status")),
        startedAt = rs.getString("started_at")?.let { Instant.parse(it) },
        completedAt = rs.getString("completed_at")?.let { Instant.parse(it) },
        cost = rs.getDouble("cost"),
    )
}