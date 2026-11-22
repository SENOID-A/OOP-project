package com.example.teratrack_ver30.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.example.teratrack_ver30.data.DriverEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for drivers.
 */
@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers ORDER BY id DESC")
    fun getAll(): Flow<List<DriverEntity>>

    @Insert
    suspend fun insert(driver: DriverEntity)

    @Update
    suspend fun update(driver: DriverEntity)

    @Delete
    suspend fun delete(driver: DriverEntity)
}