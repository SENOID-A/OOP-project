package repository

import database.Database
import location.Location
import tracking.LocationPing
import java.sql.ResultSet
import java.time.Instant

class TrackingRepository {

    fun save(p: LocationPing): LocationPing {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT OR REPLACE INTO location_pings
                (id, vehicle_id, latitude, longitude, timestamp)
                VALUES (?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, p.id)
                ps.setString(2, p.vehicleId)
                ps.setDouble(3, p.location.latitude)
                ps.setDouble(4, p.location.longitude)
                ps.setString(5, p.timestamp.toString())
                ps.executeUpdate()
            }
        }
        return p
    }

    // Oldest first. rowid = the order rows were inserted, so it is the reliable order.
    fun forVehicle(vehicleId: String): List<LocationPing> =
        Database.connect().use { conn ->
            conn.prepareStatement(
                "SELECT * FROM location_pings WHERE vehicle_id = ? ORDER BY rowid"
            ).use { ps ->
                ps.setString(1, vehicleId)
                ps.executeQuery().use { rs ->
                    val list = mutableListOf<LocationPing>()
                    while (rs.next()) list.add(toPing(rs))
                    list
                }
            }
        }

    fun latestForVehicle(vehicleId: String): LocationPing? =
        Database.connect().use { conn ->
            conn.prepareStatement(
                "SELECT * FROM location_pings WHERE vehicle_id = ? ORDER BY rowid DESC LIMIT 1"
            ).use { ps ->
                ps.setString(1, vehicleId)
                ps.executeQuery().use { rs -> if (rs.next()) toPing(rs) else null }
            }
        }

    private fun toPing(rs: ResultSet) = LocationPing(
        id = rs.getString("id"),
        vehicleId = rs.getString("vehicle_id"),
        location = Location(rs.getDouble("latitude"), rs.getDouble("longitude")),
        timestamp = Instant.parse(rs.getString("timestamp")),
    )
}