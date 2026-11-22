package com.example.teratrack_ver30.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val vehicleId: String,
    val imagePath: String? = null,
    val driverName: String? = null
): BaseEntity() {
    override fun describe(): String = "Vehicle $vehicleId (driver: ${driverName ?: "unassigned"})"
}