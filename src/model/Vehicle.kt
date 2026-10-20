package model

abstract class Vehicle (
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double
) {
    abstract fun getCoordinate()
}

class Truck (
    vehicleType: String,
    status: Boolean,
): Vehicle("0001", 12.00, 54.00){

    override fun getCoordinate() {
       println("$latitude, $longitude")
    }
}


