package com.example.teratrack_ver30.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {
    @Query("SELECT * FROM maintenance ORDER BY id DESC")
    fun getAll(): Flow<List<MaintenanceEntity>>

    @Insert
    suspend fun insert(maintenance: MaintenanceEntity)

    @Update
    suspend fun update(maintenance: MaintenanceEntity)

    @Delete
    suspend fun delete(maintenance: MaintenanceEntity)
}