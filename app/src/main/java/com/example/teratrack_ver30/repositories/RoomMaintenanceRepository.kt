package com.example.teratrack_ver30.repositories
import com.example.teratrack_ver30.data.MaintenanceDao
import com.example.teratrack_ver30.data.MaintenanceEntity

// RoomMaintenanceRepository.kt
class RoomMaintenanceRepository(private val dao: MaintenanceDao) : MaintenanceRepository {
    override fun getAll() = dao.getAll()
    override suspend fun add(m: MaintenanceEntity) = dao.insert(m)
    override suspend fun update(m: MaintenanceEntity) = dao.update(m)
    override suspend fun delete(m: MaintenanceEntity) = dao.delete(m)
}