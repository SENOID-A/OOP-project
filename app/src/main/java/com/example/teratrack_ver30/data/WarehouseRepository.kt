// WarehouseRepository.kt
package com.example.teratrack_ver30.data
import kotlinx.coroutines.flow.Flow

interface WarehouseRepository {
    fun getAll(): Flow<List<WarehouseEntity>>
}

