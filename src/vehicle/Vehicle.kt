package vehicle
import location.Location

import java.util.UUID

enum class VehicleStatus { IDLE, IN_TRIP, MAINTENANCE, OFFLINE }
enum class VehicleType { TRUCK, VAN}

data class Vehicle(
    val id: String = UUID.randomUUID().toString(),
    val plateNumber: String,
    val type: VehicleType,
    var status: VehicleStatus = VehicleStatus.IDLE,
    var currentLocation: Location,
    var odometerKm: Double = 0.0,
    var fuelLevelPct: Double = 100.0,
    val driverId: String? = null,
    val capacityKg: Double = 1000.0,
) {
    val isAvailable: Boolean
        get() = status == VehicleStatus.IDLE && fuelLevelPct > 5.0
}