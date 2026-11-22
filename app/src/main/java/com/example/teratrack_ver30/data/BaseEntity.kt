package com.example.teratrack_ver30.data

abstract class BaseEntity {
    abstract val id: Int
    // Every entity must be able to describe itself
    abstract fun describe(): String
}