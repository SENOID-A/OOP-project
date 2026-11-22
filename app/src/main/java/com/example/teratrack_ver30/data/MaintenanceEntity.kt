package com.example.teratrack_ver30.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "maintenance",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val vehicleId: Int,
    val problem: String,
    val date: String,
    val status: String = "Pending" // "Pending" or "Completed"
) : BaseEntity() {
    override fun describe(): String = "Maintenance #$id: $problem on $date ($status)"
}