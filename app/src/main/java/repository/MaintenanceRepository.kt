package repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import database.FleetDatabase
import maintenance.MaintenanceRecord
import maintenance.MaintenanceStatus
import java.time.Instant

class MaintenanceRepository(private val helper: FleetDatabase) {

    fun save(r: MaintenanceRecord): MaintenanceRecord {
        val cv = ContentValues().apply {
            put("id", r.id)
            put("vehicle_id", r.vehicleId)
            put("description", r.description)
            put("scheduled_date", r.scheduledDate.toString())
            put("status", r.status.name)
            put("started_at", r.startedAt?.toString())
            put("completed_at", r.completedAt?.toString())
            put("cost", r.cost)
        }
        helper.writableDatabase.insertWithOnConflict("maintenance_records", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return r
    }

    fun findById(id: String): MaintenanceRecord? =
        query("SELECT * FROM maintenance_records WHERE id = ?", id).firstOrNull()

    fun all(): List<MaintenanceRecord> = query("SELECT * FROM maintenance_records ORDER BY rowid")

    fun forVehicle(vehicleId: String): List<MaintenanceRecord> =
        query("SELECT * FROM maintenance_records WHERE vehicle_id = ? ORDER BY rowid", vehicleId)

    private fun query(sql: String, vararg args: String): List<MaintenanceRecord> =
        helper.writableDatabase.rawQuery(sql, arrayOf(*args)).use { c ->
            val list = mutableListOf<MaintenanceRecord>()
            while (c.moveToNext()) list.add(toRecord(c))
            list
        }

    private fun toRecord(c: Cursor) = MaintenanceRecord(
        id = c.str("id"),
        vehicleId = c.str("vehicle_id"),
        description = c.str("description"),
        scheduledDate = Instant.parse(c.str("scheduled_date")),
        status = MaintenanceStatus.valueOf(c.str("status")),
        startedAt = c.strOrNull("started_at")?.let { Instant.parse(it) },
        completedAt = c.strOrNull("completed_at")?.let { Instant.parse(it) },
        cost = c.dbl("cost"),
    )
}
