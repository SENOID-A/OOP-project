package com.example.teratrack_ver30.data


import android.content.Context
import com.example.teratrack_ver30.data.DriverRepository
import com.example.teratrack_ver30.data.MaintenanceRepository
import com.example.teratrack_ver30.data.RoomDriverRepository
import com.example.teratrack_ver30.data.RoomVehicleRepository
import com.example.teratrack_ver30.data.TripRepository
import com.example.teratrack_ver30.data.UserDatabase
import com.example.teratrack_ver30.data.VehicleRepository
import com.example.teratrack_ver30.data.WarehouseRepository
import com.exaple.teratrack_ver30.data.RoomWarehouseRepository.RoomWarehouseRepository

class AppContainer(context: Context) {
    private val db = UserDatabase.getDatabase(context)

    val vehicleRepository: VehicleRepository = RoomVehicleRepository(db.vehicleDao())
    val driverRepository: DriverRepository = RoomDriverRepository(db.driverDao())
    // Add these:
    val maintenanceRepository: MaintenanceRepository = RoomMaintenanceRepository(db.maintenanceDao())
    val tripRepository: TripRepository = RoomTripRepository(db.tripDao()) // Will create in Phase 3
    val warehouseRepository: WarehouseRepository =
        RoomWarehouseRepository(db.warehouseDao()) // Will create in Phase 3
}