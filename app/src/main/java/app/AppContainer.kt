package app

import android.content.Context
import database.FleetDatabase
import repository.DriverRepository
import repository.MaintenanceRepository
import repository.TrackingRepository
import repository.TripRepository
import repository.VehicleRepository
import maintenance.MaintenanceService
import tracking.TrackingService
import trip.TripService
import warehouse.MockWarehouseSource
import warehouse.WarehouseSource

// Builds every repository and service ONCE and shares them with all screens.
// (This is the Android replacement for the "Setup" block at the top of your main.kt.)
class AppContainer(context: Context) {
    val database = FleetDatabase.get(context)

    val vehicleRepo = VehicleRepository(database)
    val driverRepo = DriverRepository(database)
    val tripRepo = TripRepository(database)
    val maintenanceRepo = MaintenanceRepository(database)
    val trackingRepo = TrackingRepository(database)

    val tripService = TripService(tripRepo, vehicleRepo, driverRepo)
    val maintenanceService = MaintenanceService(maintenanceRepo, vehicleRepo)
    val trackingService = TrackingService(trackingRepo, vehicleRepo, tripRepo)

    // Mock data for now; swap for the classmate's database later.
    val warehouses: WarehouseSource = MockWarehouseSource()
}
