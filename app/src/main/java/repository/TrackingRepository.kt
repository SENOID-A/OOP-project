package repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import database.FleetDatabase
import location.Location
import tracking.LocationPing
import java.time.Instant

class TrackingRepository(private val helper: FleetDatabase) {

    fun save(p: LocationPing): LocationPing {
        val cv = ContentValues().apply {
            put("id", p.id)
            put("vehicle_id", p.vehicleId)
            put("latitude", p.location.latitude)
            put("longitude", p.location.longitude)
            put("timestamp", p.timestamp.toString())
        }
        helper.writableDatabase.insertWithOnConflict("location_pings", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return p
    }

    // Oldest first. rowid is the order rows were inserted, so it is the reliable order.
    fun forVehicle(vehicleId: String): List<LocationPing> =
        helper.writableDatabase.rawQuery(
            "SELECT * FROM location_pings WHERE vehicle_id = ? ORDER BY rowid",
            arrayOf(vehicleId)
        ).use { c ->
            val list = mutableListOf<LocationPing>()
            while (c.moveToNext()) list.add(toPing(c))
            list
        }

    fun latestForVehicle(vehicleId: String): LocationPing? =
        helper.writableDatabase.rawQuery(
            "SELECT * FROM location_pings WHERE vehicle_id = ? ORDER BY rowid DESC LIMIT 1",
            arrayOf(vehicleId)
        ).use { c -> if (c.moveToFirst()) toPing(c) else null }

    private fun toPing(c: Cursor) = LocationPing(
        id = c.str("id"),
        vehicleId = c.str("vehicle_id"),
        location = Location(c.dbl("latitude"), c.dbl("longitude")),
        timestamp = Instant.parse(c.str("timestamp")),
    )
}
