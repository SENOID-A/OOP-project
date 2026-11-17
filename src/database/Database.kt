package database

import java.sql.Connection
import java.sql.DriverManager

object Database {
    private const val URL = "jdbc:sqlite:fleet.db"

    fun connect(): Connection = DriverManager.getConnection(URL)

    fun init() {
        connect().use { conn ->
            conn.createStatement().use { st ->
                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS vehicles (
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
                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS drivers (
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
                st.executeUpdate(
                    """
                    CREATE TABLE IF NOT EXISTS location_pings (
                        id           TEXT PRIMARY KEY,
                        vehicle_id   TEXT NOT NULL,
                        latitude     REAL NOT NULL,
                        longitude    REAL NOT NULL,
                        timestamp    TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }

    // Empties the tables so every test run starts clean.
    fun clearAll() {
        connect().use { conn ->
            conn.createStatement().use { st ->
                st.executeUpdate("DELETE FROM vehicles")
                st.executeUpdate("DELETE FROM drivers")
                st.executeUpdate("DELETE FROM location_pings")
            }
        }
    }
}