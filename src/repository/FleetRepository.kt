package repository
import vehicle.Vehicle

class VehicleRepository {
    private val vehicles = mutableMapOf<String, Vehicle>()

    fun save(v: Vehicle): Vehicle {
        vehicles[v.id] = v
        return v
    }

    fun findById(id: String): Vehicle? = vehicles[id]

    fun findByPlate(plate: String): Vehicle? =
        vehicles.values.firstOrNull { it.plateNumber == plate }

    fun all(): List<Vehicle> = vehicles.values.toList()

    fun available(): List<Vehicle> = vehicles.values.filter { it.isAvailable }
}