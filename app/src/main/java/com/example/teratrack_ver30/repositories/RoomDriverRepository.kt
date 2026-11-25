package com.example.teratrack_ver30.repositories

import com.example.teratrack_ver30.data.DriverDao
import com.example.teratrack_ver30.data.DriverEntity
import kotlinx.coroutines.flow.Flow

class RoomDriverRepository(private val dao: DriverDao) : DriverRepository {
    override fun getAll(): Flow<List<DriverEntity>> = dao.getAll()
    override suspend fun add(driver: DriverEntity) = dao.insert(driver)
    override suspend fun update(driver: DriverEntity) = dao.update(driver)
    override suspend fun delete(driver: DriverEntity) = dao.delete(driver)
}