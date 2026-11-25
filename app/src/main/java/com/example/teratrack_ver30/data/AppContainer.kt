package com.example.teratrack_ver30.data

import android.content.Context
import com.example.teratrack_ver30.repositories.DriverRepository
import com.example.teratrack_ver30.repositories.MaintenanceRepository
import com.example.teratrack_ver30.repositories.RoomDriverRepository
import com.example.teratrack_ver30.repositories.RoomMaintenanceRepository
import com.example.teratrack_ver30.repositories.RoomTripRepository
import com.example.teratrack_ver30.repositories.RoomVehicleRepository
import com.example.teratrack_ver30.repositories.RoomWarehouseRepository
import com.example.teratrack_ver30.repositories.TripRepository
import com.example.teratrack_ver30.repositories.VehicleRepository
import com.example.teratrack_ver30.repositories.WarehouseRepository

//Repository builder
class AppContainer(context: Context) {
    private val db = UserDatabase.getDatabase(context)

    val vehicleRepository: VehicleRepository = RoomVehicleRepository(db.vehicleDao())
    val driverRepository: DriverRepository = RoomDriverRepository(db.driverDao())
    // Add these:
    val maintenanceRepository: MaintenanceRepository =
        RoomMaintenanceRepository(db.maintenanceDao())
    val tripRepository: TripRepository = RoomTripRepository(db.tripDao()) // Will create in Phase 3
    val warehouseRepository: WarehouseRepository =
        RoomWarehouseRepository(db.warehouseDao()) // Will create in Phase 3
}