package warehouse

import location.Location
import java.io.File
import java.sql.DriverManager
import java.sql.ResultSet

// Reads warehouses from the classmate's SQLite file.
// We NEVER write to their database, so we open it for reading only.
//
// The table/column names below are GUESSES. When the file arrives, open it,
// look at the real names, and change ONLY the values in the constructor
// (no need to touch the logic below).
class SqliteWarehouseSource(
    dbPath: String,
    private val table: String = "warehouses",
    private val idCol: String = "id",
    private val nameCol: String = "name",
    private val provinceCol: String = "province",
    private val latCol: String = "latitude",
    private val lonCol: String = "longitude",
    private val capacityCol: String = "capacity_kg",
) : WarehouseSource {

    private val url: String

    init {
        require(File(dbPath).exists()) { "Warehouse database not found: $dbPath" }
        url = "jdbc:sqlite:$dbPath"
    }

    override fun all(): List<Warehouse> =
        DriverManager.getConnection(url).use { conn ->
            conn.createStatement().use { st ->
                st.executeQuery("SELECT * FROM $table").use { rs ->
                    val result = mutableListOf<Warehouse>()
                    while (rs.next()) result.add(toWarehouse(rs))
                    result
                }
            }
        }

    override fun findById(id: String): Warehouse? =
        DriverManager.getConnection(url).use { conn ->
            conn.prepareStatement("SELECT * FROM $table WHERE $idCol = ?").use { ps ->
                ps.setString(1, id)
                ps.executeQuery().use { rs -> if (rs.next()) toWarehouse(rs) else null }
            }
        }

    // Converts ONE database row into ONE Warehouse object.
    private fun toWarehouse(rs: ResultSet) = Warehouse(
        id = rs.getString(idCol),
        name = rs.getString(nameCol),
        province = rs.getString(provinceCol) ?: "",
        location = Location(rs.getDouble(latCol), rs.getDouble(lonCol)),
    )
}