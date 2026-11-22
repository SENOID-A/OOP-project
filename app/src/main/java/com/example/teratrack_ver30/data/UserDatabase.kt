package com.example.teratrack_ver30.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.room3.RoomDatabase.Callback

@Database(
    entities = [
        UserEntity::class,
        VehicleEntity::class,
        DriverEntity::class,
        MaintenanceEntity::class,
        TripEntity::class,
        WarehouseEntity::class
    ],
    version = 4, // <-- CHANGED TO 4
    exportSchema = false
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun driverDao(): DriverDao
    abstract fun maintenanceDao(): MaintenanceDao
    abstract fun tripDao(): TripDao
    abstract fun warehouseDao(): WarehouseDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase? = null

        fun getDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "teratrack.db"
                )
                    .addCallback(object : Callback() {
                        override suspend fun onCreate(connection: SQLiteConnection) {
                            super.onCreate(connection)

                            // <-- UPDATED TO INCLUDE LATITUDE AND LONGITUDE
                            val query = """
                            INSERT INTO warehouses (warehouseName, province, latitude, longitude) VALUES 
                            ('J&T Express Delivery Distribution Center (Dasmariñas)', 'Cavite', 14.3294000, 120.9367000),
                            ('J&T Calamba DC', 'Laguna', 14.2091219, 121.1554600),
                            ('J&T Batangas DC', 'Batangas', 14.0894929, 121.1368192),
                            ('J&T Main Warehouse Taytay', 'Rizal', 14.564637, 121.121234),
                            ('J&T Express Lucena DH3', 'Quezon', 13.9444088, 121.6122594)
                        """.trimIndent()

                            connection.prepare(query).use { statement ->
                                statement.step()
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}