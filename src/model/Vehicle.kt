package model

//Abstract Super Class
abstract class Vehicle (
    val vehicleID: String,
    val latitude: Double,
    val longitude: Double
) {
    abstract fun getCoordinate(): String
}

class Truck (
    vehicleID: String,
    latitude: Double,
    longitude: Double,
    val vehicleType: String,
    val status: Boolean,
): Vehicle (vehicleID, latitude, longitude) {

    override fun getCoordinate():  String {
        return "Truck $vehicleID coordinates: $latitude, $longitude"
    }
}

class Motorcycle (
    vehicleID: String,
    latitude: Double,
    longitude: Double,
    val vehicleType: String,
    val status: Boolean,
): Vehicle (vehicleID, latitude, longitude) {

    override fun getCoordinate(): String {
        return "Motorcycle $vehicleID coordinates: $latitude, $longitude"
    }
}
