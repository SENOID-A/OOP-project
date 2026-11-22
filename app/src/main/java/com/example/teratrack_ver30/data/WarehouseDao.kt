package com.example.teratrack_ver30.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseDao {
    @Query("SELECT * FROM warehouses")
    fun getAll(): Flow<List<WarehouseEntity>>

    @Insert
    suspend fun insertAll(warehouses: List<WarehouseEntity>)
}