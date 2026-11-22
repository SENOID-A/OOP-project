// MaintenanceRepository.kt
package com.example.teratrack_ver30.data
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {
    fun getAll(): Flow<List<MaintenanceEntity>>
    suspend fun add(m: MaintenanceEntity)
    suspend fun update(m: MaintenanceEntity)
    suspend fun delete(m: MaintenanceEntity)
}

