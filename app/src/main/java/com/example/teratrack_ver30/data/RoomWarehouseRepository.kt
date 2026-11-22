package com.exaple.teratrack_ver30.data.RoomWarehouseRepository
import com.example.teratrack_ver30.data.WarehouseDao
import com.example.teratrack_ver30.data.WarehouseRepository

// RoomWarehouseRepository.kt
class RoomWarehouseRepository(private val dao: WarehouseDao) : WarehouseRepository {
    override fun getAll() = dao.getAll()
}