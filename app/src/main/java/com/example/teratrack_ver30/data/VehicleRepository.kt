package com.example.teratrack_ver30.data

import kotlinx.coroutines.flow.Flow

// Interface for Abstraction (SOLID - Dependency Inversion Principle)
interface VehicleRepository {
    fun getAll(): Flow<List<VehicleEntity>>
    fun getUnassigned(): Flow<List<VehicleEntity>>
    suspend fun add(vehicle: VehicleEntity)
    suspend fun update(vehicle: VehicleEntity)
    suspend fun delete(vehicle: VehicleEntity)
}