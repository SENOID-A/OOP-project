package repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import database.FleetDatabase
import location.Location
import vehicle.Vehicle
import vehicle.VehicleStatus
import vehicle.VehicleType

class VehicleRepository(private val helper: FleetDatabase) {

    fun save(v: Vehicle): Vehicle {
        val cv = ContentValues().apply {
            put("id", v.id)
            put("plate_number", v.plateNumber)
            put("type", v.type.name)
            put("status", v.status.name)
            put("latitude", v.currentLocation.latitude)
            put("longitude", v.currentLocation.longitude)
            put("odometer_km", v.odometerKm)
            put("fuel_level_pct", v.fuelLevelPct)
            put("driver_id", v.driverId)          // null is stored as NULL
            put("capacity_kg", v.capacityKg)
        }
        // CONFLICT_REPLACE = insert, or overwrite the row with the same id (upsert)
        helper.writableDatabase.insertWithOnConflict("vehicles", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return v
    }

    fun findById(id: String): Vehicle? =
        query("SELECT * FROM vehicles WHERE id = ?", id).firstOrNull()

    fun findByPlate(plate: String): Vehicle? =
        query("SELECT * FROM vehicles WHERE plate_number = ?", plate).firstOrNull()

    fun all(): List<Vehicle> = query("SELECT * FROM vehicles ORDER BY rowid")

    fun available(): List<Vehicle> = all().filter { it.isAvailable }

    private fun query(sql: String, vararg args: String): List<Vehicle> =
        helper.writableDatabase.rawQuery(sql, arrayOf(*args)).use { c ->
            val list = mutableListOf<Vehicle>()
            while (c.moveToNext()) list.add(toVehicle(c))
            list
        }

    private fun toVehicle(c: Cursor) = Vehicle(
        id = c.str("id"),
        plateNumber = c.str("plate_number"),
        type = VehicleType.valueOf(c.str("type")),
        status = VehicleStatus.valueOf(c.str("status")),
        currentLocation = Location(c.dbl("latitude"), c.dbl("longitude")),
        odometerKm = c.dbl("odometer_km"),
        fuelLevelPct = c.dbl("fuel_level_pct"),
        driverId = c.strOrNull("driver_id"),
        capacityKg = c.dbl("capacity_kg"),
    )
}
