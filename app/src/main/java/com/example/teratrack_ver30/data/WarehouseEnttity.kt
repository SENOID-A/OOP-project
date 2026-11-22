package com.example.teratrack_ver30.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "warehouses")
data class WarehouseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val warehouseName: String,
    val province: String,
    val latitude: Double,   // <-- ADD THIS
    val longitude: Double   // <-- ADD THIS
)