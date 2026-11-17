package repository

import database.Database
import user.Driver
import java.sql.ResultSet
import java.sql.Types

class DriverRepository {

    fun save(d: Driver): Driver {
        Database.connect().use { conn ->
            conn.prepareStatement(
                """
                INSERT OR REPLACE INTO drivers
                (staff_id, name, position, address, vehicle, is_assigned, on_duty)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, d.staffId)
                ps.setString(2, d.name)
                ps.setString(3, d.position)
                ps.setString(4, d.address)
                ps.setString(5, d.vehicle)
                ps.setInt(6, if (d.isAssigned) 1 else 0)

                val duty = d.onDuty
                if (duty == null) ps.setNull(7, Types.INTEGER)
                else ps.setInt(7, if (duty) 1 else 0)

                ps.executeUpdate()
            }
        }
        return d
    }

    fun findById(id: String): Driver? =
        Database.connect().use { conn ->
            conn.prepareStatement("SELECT * FROM drivers WHERE staff_id = ?").use { ps ->
                ps.setString(1, id)
                ps.executeQuery().use { rs -> if (rs.next()) toDriver(rs) else null }
            }
        }

    fun all(): List<Driver> =
        Database.connect().use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT * FROM drivers").use { rs ->
                    val list = mutableListOf<Driver>()
                    while (rs.next()) list.add(toDriver(rs))
                    list
                }
            }
        }

    private fun toDriver(rs: ResultSet): Driver {
        val duty = rs.getInt("on_duty")
        val onDuty: Boolean? = if (rs.wasNull()) null else duty == 1
        return Driver(
            staffId = rs.getString("staff_id"),
            name = rs.getString("name"),
            position = rs.getString("position"),
            address = rs.getString("address"),
            vehicle = rs.getString("vehicle"),
            isAssigned = rs.getInt("is_assigned") == 1,
            onDuty = onDuty,
        )
    }
}