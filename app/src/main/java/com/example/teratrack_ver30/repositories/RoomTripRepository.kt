package com.example.teratrack_ver30.repositories
import com.example.teratrack_ver30.data.TripDao
import com.example.teratrack_ver30.data.TripEntity

// RoomTripRepository.kt
class RoomTripRepository(private val dao: TripDao) : TripRepository {
    override fun getAll() = dao.getAll()
    override suspend fun add(t: TripEntity) = dao.insert(t)
    override suspend fun update(t: TripEntity) = dao.update(t)
    override suspend fun delete(t: TripEntity) = dao.delete(t)
}