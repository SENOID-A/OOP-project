package com.example.teratrack_ver30.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(entity = VehicleEntity::class, parentColumns = ["id"], childColumns = ["vehicleId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class TripEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val vehicleId: Int,
    val driverName: String,
    val description: String,
    val startPoint: String,
    val destination: String,
    val date: String,
    val status: String = "Pending"
) : BaseEntity() {
    override fun describe(): String = "Trip #$id: $startPoint to $destination ($status)"
}