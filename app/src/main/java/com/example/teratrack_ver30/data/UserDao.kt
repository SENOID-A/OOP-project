package com.example.teratrack_ver30.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Update
import androidx.room3.Delete
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface UserDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(user: UserEntity)

    // READ
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun find(username: String): UserEntity?

    // UPDATE (Room uses the PrimaryKey to know which row to update)
    @Update
    suspend fun update(user: UserEntity)

    // DELETE
    @Delete
    suspend fun delete(user: UserEntity)
}