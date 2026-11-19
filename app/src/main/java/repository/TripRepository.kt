package repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import database.FleetDatabase
import location.Location
import trip.Trip
import trip.TripStatus
import java.time.Instant

class TripRepository(private val helper: FleetDatabase) {

    fun save(t: Trip): Trip {
        val cv = ContentValues().apply {
            put("id", t.id)
            put("vehicle_id", t.vehicleId)
            put("driver_id", t.driverId)
            put("origin_lat", t.origin.latitude)
            put("origin_lon", t.origin.longitude)
            put("dest_lat", t.destination.latitude)
            put("dest_lon", t.destination.longitude)
            put("scheduled_start", t.scheduledStart.toString())
            put("cargo_weight_kg", t.cargoWeightKg)
            put("status", t.status.name)
            put("actual_start", t.actualStart?.toString())
            put("actual_end", t.actualEnd?.toString())
            put("planned_distance_km", t.plannedDistanceKm)
            put("actual_distance_km", t.actualDistanceKm)
            put("cancellation_reason", t.cancellationReason)
        }
        helper.writableDatabase.insertWithOnConflict("trips", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return t
    }

    fun findById(id: String): Trip? =
        query("SELECT * FROM trips WHERE id = ?", id).firstOrNull()

    fun all(): List<Trip> = query("SELECT * FROM trips ORDER BY rowid")

    fun activeForVehicle(vehicleId: String): Trip? =
        query("SELECT * FROM trips WHERE vehicle_id = ? AND status = 'IN_PROGRESS'", vehicleId).firstOrNull()

    fun forVehicle(vehicleId: String): List<Trip> =
        query("SELECT * FROM trips WHERE vehicle_id = ? ORDER BY rowid", vehicleId)

    fun forDriver(driverId: String): List<Trip> =
        query("SELECT * FROM trips WHERE driver_id = ? ORDER BY rowid", driverId)

    private fun query(sql: String, vararg args: String): List<Trip> =
        helper.writableDatabase.rawQuery(sql, arrayOf(*args)).use { c ->
            val list = mutableListOf<Trip>()
            while (c.moveToNext()) list.add(toTrip(c))
            list
        }

    private fun toTrip(c: Cursor) = Trip(
        id = c.str("id"),
        vehicleId = c.str("vehicle_id"),
        driverId = c.str("driver_id"),
        origin = Location(c.dbl("origin_lat"), c.dbl("origin_lon")),
        destination = Location(c.dbl("dest_lat"), c.dbl("dest_lon")),
        scheduledStart = Instant.parse(c.str("scheduled_start")),
        cargoWeightKg = c.dbl("cargo_weight_kg"),
        status = TripStatus.valueOf(c.str("status")),
        actualStart = c.strOrNull("actual_start")?.let { Instant.parse(it) },
        actualEnd = c.strOrNull("actual_end")?.let { Instant.parse(it) },
        plannedDistanceKm = c.dbl("planned_distance_km"),
        actualDistanceKm = c.dbl("actual_distance_km"),
        cancellationReason = c.strOrNull("cancellation_reason"),
    )
}
