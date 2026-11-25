package com.example.teratrack_ver30.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val passwordHash: String,
    val salt: String,
    val favouriteWord: String
)
