package com.example.teratrack_ver30.repositories
import com.example.teratrack_ver30.data.WarehouseDao
import com.example.teratrack_ver30.repositories.WarehouseRepository

// RoomWarehouseRepository.kt
class RoomWarehouseRepository(private val dao: WarehouseDao) : WarehouseRepository {
    override fun getAll() = dao.getAll()
}