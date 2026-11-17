package repository

import database.Database
import location.Location
import trip.Trip
import trip.TripStatus
import java.sql.ResultSet
import java.time.Instant

class TripRepository {

    fun save(t: Trip): Trip {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT OR REPLACE INTO trips
                (id, vehicle_id, driver_id, origin_lat, origin_lon, dest_lat, dest_lon,
                 scheduled_start, cargo_weight_kg, status, actual_start, actual_end,
                 planned_distance_km, actual_distance_km, cancellation_reason)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, t.id)
                ps.setString(2, t.vehicleId)
                ps.setString(3, t.driverId)
                ps.setDouble(4, t.origin.latitude)
                ps.setDouble(5, t.origin.longitude)
                ps.setDouble(6, t.destination.latitude)
                ps.setDouble(7, t.destination.longitude)
                ps.setString(8, t.scheduledStart.toString())
                ps.setDouble(9, t.cargoWeightKg)
                ps.setString(10, t.status.name)
                ps.setString(11, t.actualStart?.toString())
                ps.setString(12, t.actualEnd?.toString())
                ps.setDouble(13, t.plannedDistanceKm)
                ps.setDouble(14, t.actualDistanceKm)
                ps.setString(15, t.cancellationReason)
                ps.executeUpdate()
            }
        }
        return t
    }

    fun findById(id: String): Trip? =
        queryList("SELECT * FROM trips WHERE id = ?", id).firstOrNull()

    fun all(): List<Trip> = queryList("SELECT * FROM trips")

    fun activeForVehicle(vehicleId: String): Trip? =
        queryList(
            "SELECT * FROM trips WHERE vehicle_id = ? AND status = 'IN_PROGRESS'",
            vehicleId
        ).firstOrNull()

    fun forVehicle(vehicleId: String): List<Trip> =
        queryList("SELECT * FROM trips WHERE vehicle_id = ?", vehicleId)

    fun forDriver(driverId: String): List<Trip> =
        queryList("SELECT * FROM trips WHERE driver_id = ?", driverId)

    private fun queryList(sql: String, param: String? = null): List<Trip> =
        Database.connect().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                if (param != null) ps.setString(1, param)
                ps.executeQuery().use { rs ->
                    val list = mutableListOf<Trip>()
                    while (rs.next()) list.add(toTrip(rs))
                    list
                }
            }
        }

    private fun toTrip(rs: ResultSet) = Trip(
        id = rs.getString("id"),
        vehicleId = rs.getString("vehicle_id"),
        driverId = rs.getString("driver_id"),
        origin = Location(rs.getDouble("origin_lat"), rs.getDouble("origin_lon")),
        destination = Location(rs.getDouble("dest_lat"), rs.getDouble("dest_lon")),
        scheduledStart = Instant.parse(rs.getString("scheduled_start")),
        cargoWeightKg = rs.getDouble("cargo_weight_kg"),
        status = TripStatus.valueOf(rs.getString("status")),
        actualStart = rs.getString("actual_start")?.let { Instant.parse(it) },
        actualEnd = rs.getString("actual_end")?.let { Instant.parse(it) },
        plannedDistanceKm = rs.getDouble("planned_distance_km"),
        actualDistanceKm = rs.getDouble("actual_distance_km"),
        cancellationReason = rs.getString("cancellation_reason"),
    )
}