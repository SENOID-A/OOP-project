package warehouse

import location.Location

// A warehouse owned by the other project (Warehouse and Distribution Locator).
// Our fleet app only READS it, so everything is val (read-only).
data class Warehouse(
    val id: String,
    val name: String,
    val province: String,
    val location: Location,
)