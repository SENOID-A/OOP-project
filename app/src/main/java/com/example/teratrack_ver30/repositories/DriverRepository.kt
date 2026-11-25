package com.example.teratrack_ver30.repositories

import com.example.teratrack_ver30.data.DriverEntity
import kotlinx.coroutines.flow.Flow

interface DriverRepository {
    fun getAll(): Flow<List<DriverEntity>>
    suspend fun add(driver: DriverEntity)
    suspend fun update(driver: DriverEntity)
    suspend fun delete(driver: DriverEntity)
}