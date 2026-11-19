package repository

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import database.FleetDatabase
import user.Driver

class DriverRepository(private val helper: FleetDatabase) {

    fun save(d: Driver): Driver {
        val cv = ContentValues().apply {
            put("staff_id", d.staffId)
            put("name", d.name)
            put("position", d.position)
            put("address", d.address)
            put("vehicle", d.vehicle)
            put("is_assigned", if (d.isAssigned) 1 else 0)
            // onDuty can be null: null -> NULL, true -> 1, false -> 0
            put("on_duty", d.onDuty?.let { if (it) 1 else 0 })
        }
        helper.writableDatabase.insertWithOnConflict("drivers", null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return d
    }

    fun findById(id: String): Driver? =
        query("SELECT * FROM drivers WHERE staff_id = ?", id).firstOrNull()

    fun all(): List<Driver> = query("SELECT * FROM drivers ORDER BY rowid")

    private fun query(sql: String, vararg args: String): List<Driver> =
        helper.writableDatabase.rawQuery(sql, arrayOf(*args)).use { c ->
            val list = mutableListOf<Driver>()
            while (c.moveToNext()) list.add(toDriver(c))
            list
        }

    private fun toDriver(c: Cursor) = Driver(
        staffId = c.str("staff_id"),
        name = c.str("name"),
        position = c.str("position"),
        address = c.str("address"),
        vehicle = c.str("vehicle"),
        isAssigned = c.int("is_assigned") == 1,
        onDuty = c.intOrNull("on_duty")?.let { it == 1 },
    )
}
