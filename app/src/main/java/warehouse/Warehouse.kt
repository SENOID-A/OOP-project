package warehouse

import location.Location

// Matches the classmate's table: warehouse_id, warehouse_name, province, latitude, longitude.
// Read-only: our app never changes this data.
data class Warehouse(
    val id: String,        // their warehouse_id is a number; we keep it as text
    val name: String,
    val province: String,
    val location: Location,
)