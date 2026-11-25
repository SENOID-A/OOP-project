package com.example.teratrack_ver30.repositories

import com.example.teratrack_ver30.data.VehicleDao
import com.example.teratrack_ver30.data.VehicleEntity
import kotlinx.coroutines.flow.Flow

// Implementation of the interface (Polymorphism)
class RoomVehicleRepository(private val dao: VehicleDao) : VehicleRepository {
    override fun getAll(): Flow<List<VehicleEntity>> = dao.getAll()
    override fun getUnassigned(): Flow<List<VehicleEntity>> = dao.getUnassigned()
    override suspend fun add(vehicle: VehicleEntity) = dao.insert(vehicle)
    override suspend fun update(vehicle: VehicleEntity) = dao.update(vehicle)
    override suspend fun delete(vehicle: VehicleEntity) = dao.delete(vehicle)
}