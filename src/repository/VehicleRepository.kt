package repository

import database.Database
import location.Location
import vehicle.Vehicle
import vehicle.VehicleStatus
import vehicle.VehicleType
import java.sql.ResultSet

class VehicleRepository {

    fun save(v: Vehicle): Vehicle {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT OR REPLACE INTO vehicles
                (id, plate_number, type, status, latitude, longitude,
                 odometer_km, fuel_level_pct, driver_id, capacity_kg)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, v.id)
                ps.setString(2, v.plateNumber)
                ps.setString(3, v.type.name)
                ps.setString(4, v.status.name)
                ps.setDouble(5, v.currentLocation.latitude)
                ps.setDouble(6, v.currentLocation.longitude)
                ps.setDouble(7, v.odometerKm)
                ps.setDouble(8, v.fuelLevelPct)
                ps.setString(9, v.driverId)
                ps.setDouble(10, v.capacityKg)
                ps.executeUpdate()
            }
        }
        return v
    }

    fun findById(id: String): Vehicle? = queryOne("SELECT * FROM vehicles WHERE id = ?", id)

    fun findByPlate(plate: String): Vehicle? =
        queryOne("SELECT * FROM vehicles WHERE plate_number = ?", plate)

    fun all(): List<Vehicle> =
        Database.connect().use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT * FROM vehicles").use { rs ->
                    val list = mutableListOf<Vehicle>()
                    while (rs.next()) list.add(toVehicle(rs))
                    list
                }
            }
        }

    fun available(): List<Vehicle> = all().filter { it.isAvailable }

    // Runs a query with ONE "?" value and returns the first row, or null.
    private fun queryOne(sql: String, param: String): Vehicle? =
        Database.connect().use { conn ->
            conn.prepareStatement(sql).use { ps ->
                ps.setString(1, param)
                ps.executeQuery().use { rs -> if (rs.next()) toVehicle(rs) else null }
            }
        }

    // Turns one database row back into a Vehicle object.
    private fun toVehicle(rs: ResultSet) = Vehicle(
        id = rs.getString("id"),
        plateNumber = rs.getString("plate_number"),
        type = VehicleType.valueOf(rs.getString("type")),
        status = VehicleStatus.valueOf(rs.getString("status")),
        currentLocation = Location(rs.getDouble("latitude"), rs.getDouble("longitude")),
        odometerKm = rs.getDouble("odometer_km"),
        fuelLevelPct = rs.getDouble("fuel_level_pct"),
        driverId = rs.getString("driver_id"),
        capacityKg = rs.getDouble("capacity_kg"),
    )
}