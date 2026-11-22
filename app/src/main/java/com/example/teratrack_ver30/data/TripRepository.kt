// TripRepository.kt
package com.example.teratrack_ver30.data
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAll(): Flow<List<TripEntity>>
    suspend fun add(t: TripEntity)
    suspend fun update(t: TripEntity)
    suspend fun delete(t: TripEntity)
}