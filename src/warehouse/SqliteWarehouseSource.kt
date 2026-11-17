package warehouse

import location.Location
import org.sqlite.SQLiteConfig
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

// Reads the classmate's warehouse database. Opened READ-ONLY so we can never change it.
class SqliteWarehouseSource(private val dbPath: String) : WarehouseSource {

    init {
        require(File(dbPath).exists()) { "Warehouse database not found: $dbPath" }
    }

    private fun connect(): Connection {
        val config = SQLiteConfig()
        config.setReadOnly(true)
        return DriverManager.getConnection("jdbc:sqlite:$dbPath", config.toProperties())
    }

    override fun all(): List<Warehouse> =
        connect().use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT * FROM warehouses ORDER BY warehouse_id").use { rs ->
                    val list = mutableListOf<Warehouse>()
                    while (rs.next()) list.add(toWarehouse(rs))
                    list
                }
            }
        }

    override fun findById(id: String): Warehouse? =
        connect().use { conn ->
            conn.prepareStatement("SELECT * FROM warehouses WHERE warehouse_id = ?").use { ps ->
                ps.setString(1, id)
                ps.executeQuery().use { rs -> if (rs.next()) toWarehouse(rs) else null }
            }
        }

    private fun toWarehouse(rs: ResultSet) = Warehouse(
        id = rs.getInt("warehouse_id").toString(),
        name = rs.getString("warehouse_name"),
        province = rs.getString("province"),
        location = Location(rs.getDouble("latitude"), rs.getDouble("longitude")),
    )
}