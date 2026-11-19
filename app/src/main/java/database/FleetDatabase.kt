package database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Android version of Database.kt.
// SQLiteOpenHelper creates fleet.db the first time the app runs and calls onCreate().
// The data lives inside the app's private storage, so it survives closing the app.
class FleetDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context, "fleet.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE vehicles (
                id              TEXT PRIMARY KEY,
                plate_number    TEXT NOT NULL UNIQUE,
                type            TEXT NOT NULL,
                status          TEXT NOT NULL,
                latitude        REAL NOT NULL,
                longitude       REAL NOT NULL,
                odometer_km     REAL NOT NULL,
                fuel_level_pct  REAL NOT NULL,
                driver_id       TEXT,
                capacity_kg     REAL NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE drivers (
                staff_id     TEXT PRIMARY KEY,
                name         TEXT NOT NULL,
                position     TEXT NOT NULL,
                address      TEXT NOT NULL,
                vehicle      TEXT NOT NULL,
                is_assigned  INTEGER NOT NULL,
                on_duty      INTEGER
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE trips (
                id                    TEXT PRIMARY KEY,
                vehicle_id            TEXT NOT NULL,
                driver_id             TEXT NOT NULL,
                origin_lat            REAL NOT NULL,
                origin_lon            REAL NOT NULL,
                dest_lat              REAL NOT NULL,
                dest_lon              REAL NOT NULL,
                scheduled_start       TEXT NOT NULL,
                cargo_weight_kg       REAL NOT NULL,
                status                TEXT NOT NULL,
                actual_start          TEXT,
                actual_end            TEXT,
                planned_distance_km   REAL NOT NULL,
                actual_distance_km    REAL NOT NULL,
                cancellation_reason   TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE location_pings (
                id           TEXT PRIMARY KEY,
                vehicle_id   TEXT NOT NULL,
                latitude     REAL NOT NULL,
                longitude    REAL NOT NULL,
                timestamp    TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE maintenance_records (
                id              TEXT PRIMARY KEY,
                vehicle_id      TEXT NOT NULL,
                description     TEXT NOT NULL,
                scheduled_date  TEXT NOT NULL,
                status          TEXT NOT NULL,
                started_at      TEXT,
                completed_at    TEXT,
                cost            REAL NOT NULL
            )
            """.trimIndent()
        )
    }

    // Called when you raise the version number above. Simple approach for a student
    // project: drop everything and rebuild. (A real app would migrate the data.)
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        for (t in listOf("vehicles", "drivers", "trips", "location_pings", "maintenance_records")) {
            db.execSQL("DROP TABLE IF EXISTS $t")
        }
        onCreate(db)
    }

    // Empties every table (handy for a "Reset demo data" button).
    fun clearAll() {
        val db = writableDatabase
        for (t in listOf("vehicles", "drivers", "trips", "location_pings", "maintenance_records")) {
            db.execSQL("DELETE FROM $t")
        }
    }

    companion object {
        @Volatile private var instance: FleetDatabase? = null

        // One shared helper for the whole app.
        fun get(context: Context): FleetDatabase =
            instance ?: synchronized(this) {
                instance ?: FleetDatabase(context.applicationContext).also { instance = it }
            }
    }
}
