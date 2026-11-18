package app

import location.Location
import user.Driver
import vehicle.Vehicle
import vehicle.VehicleType

// Gives the demo something to show on first launch. Does nothing if data already exists.
object SeedData {
    fun seedIfEmpty(c: AppContainer) {
        if (c.vehicleRepo.all().isNotEmpty()) return

        c.vehicleRepo.save(Vehicle(plateNumber = "ABC-123", type = VehicleType.TRUCK,
            currentLocation = Location(14.5995, 120.9842), capacityKg = 1000.0))
        c.vehicleRepo.save(Vehicle(plateNumber = "DEF-456", type = VehicleType.VAN,
            currentLocation = Location(14.3294, 120.9367), capacityKg = 800.0))
        c.vehicleRepo.save(Vehicle(plateNumber = "GHI-789", type = VehicleType.VAN,
            currentLocation = Location(14.0894929, 121.1368192), capacityKg = 600.0))

        c.driverRepo.save(Driver("D-1", "Juan", "Driver", "Manila", "ABC-123", true, true))
        c.driverRepo.save(Driver("D-2", "Pedro", "Driver", "Cavite", "DEF-456", true, true))
    }
}
