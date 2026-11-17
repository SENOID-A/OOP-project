package warehouse

import location.Location

// Fake warehouses so we can build and test BEFORE the classmate sends their database.
// When their data arrives, write a new class (e.g. SqliteWarehouseSource) that
// implements WarehouseSource, and change ONE line in main. Nothing else changes.
class MockWarehouseSource : WarehouseSource {
    private val warehouses = listOf(
        Warehouse("W-1", "Manila Main Warehouse", "Manila", Location(14.6091, 120.9672)),
        Warehouse("W-2", "Dasmarinas Hub", "Dasmarinas", Location(14.3294, 120.9367)),
        Warehouse("W-3", "Batangas Port Depot", "Batangas", Location(13.7565, 121.0583)),
    )

    override fun all(): List<Warehouse> = warehouses

    override fun findById(id: String): Warehouse? = warehouses.firstOrNull { it.id == id }
}