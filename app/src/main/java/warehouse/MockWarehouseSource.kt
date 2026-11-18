package warehouse

import location.Location

class MockWarehouseSource : WarehouseSource {
    private val warehouses = listOf(
        Warehouse("1", "J&T Express Delivery Distribution Center (Dasmariñas)", "Cavite", Location(14.3294, 120.9367)),
        Warehouse("2", "J&T Calamba DC", "Laguna", Location(14.2091219, 121.15546)),
        Warehouse("3", "J&T Batangas DC", "Batangas", Location(14.0894929, 121.1368192)),
        Warehouse("4", "J&T Main Warehouse Taytay", "Rizal", Location(14.564637, 121.121234)),
        Warehouse("5", "J&T Express Lucena DH3", "Quezon", Location(13.9444088, 121.6122594)),
    )

    override fun all(): List<Warehouse> = warehouses

    override fun findById(id: String): Warehouse? = warehouses.firstOrNull { it.id == id }
}