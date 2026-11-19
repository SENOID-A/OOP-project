package com.example.teratrack

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import app.FleetApp
import maintenance.MaintenanceRecord
import trip.Trip
import trip.TripStatus
import vehicle.Vehicle
import warehouse.Warehouse

/** UI-facing access point. Activities do not call repositories or services directly. */
class FleetViewModel(application: Application) : AndroidViewModel(application) {
    private val container = (application as FleetApp).container

    fun vehicles(): List<Vehicle> = container.vehicleRepo.all()
    fun trips(): List<Trip> = container.tripRepo.all()
    fun maintenanceRecords(): List<MaintenanceRecord> = container.maintenanceRepo.all()
    fun warehouses(): List<Warehouse> = container.warehouses.all()

    fun advanceTrip(tripId: String): Trip {
        val trip = container.tripRepo.findById(tripId) ?: error("Trip not found")
        return when (trip.status) {
            TripStatus.SCHEDULED -> container.tripService.startTrip(tripId)
            TripStatus.IN_PROGRESS -> container.tripService.completeTrip(tripId)
            else -> error("This trip can no longer be changed")
        }
    }
}
