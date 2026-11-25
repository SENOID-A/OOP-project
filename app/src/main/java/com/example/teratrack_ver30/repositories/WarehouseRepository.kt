// WarehouseRepository.kt
package com.example.teratrack_ver30.repositories
import com.example.teratrack_ver30.data.WarehouseEntity
import kotlinx.coroutines.flow.Flow

interface WarehouseRepository {
    fun getAll(): Flow<List<WarehouseEntity>>
}

