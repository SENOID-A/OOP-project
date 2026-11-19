package warehouse

import location.Location

// The CONTRACT between our project and the classmate's Warehouse project.
// The rest of our app only talks to this interface, so it doesn't care
// whether the data is fake (mock), from their SQLite file, or from an API.
interface WarehouseSource {
    fun all(): List<Warehouse>
    fun findById(id: String): Warehouse?

    // Default implementation: works for ANY source, no need to rewrite it.
    fun nearestTo(point: Location): Warehouse? =
        all().minByOrNull { it.location.distanceTo(point) }
}