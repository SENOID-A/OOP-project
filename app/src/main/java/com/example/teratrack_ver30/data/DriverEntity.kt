package com.example.teratrack_ver30.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

/**
 * Room entity for the "drivers" table.
 * vehicleId is a Foreign Key pointing to VehicleEntity.id.
 * SET_NULL means: if a vehicle is deleted, the driver stays but becomes unassigned.
 */
@Entity(
    tableName = "drivers",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class DriverEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val name: String,
    val address: String,
    val phone: String,
    val vehicleId: Int? = null
) : BaseEntity() {
    override fun describe(): String = "Driver $name, $phone"
}