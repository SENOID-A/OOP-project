package Location
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

abstract class Location(
    val latitude: Double,
    val longitude: Double,
    val address: String
) {
    fun distanceTo(other: Location): Double {
        val radius = 6371.0 // Earth's radius in meters

        val dlat = Math.toRadians(other.latitude - this.latitude)
        val dlon = Math.toRadians(other.longitude - this.longitude)

        val a = sin(dlat / 2) * Math.sin(dlat / 2) +
                cos(Math.toRadians(this.latitude)) * cos(Math.toRadians(other.latitude)) *
                sin(dlon / 2) * Math.sin(dlon / 2)

        val c = 2 * atan2(Math.sqrt(a), sqrt(1 - a))

        return radius * c
    }
}

//nasugbu
//14.04° N to 14.10° N and a longitude range of 120.35° E to 120.69° E

class WareHouseLocation(
    latitude: Double,
    longitude: Double,
    address: String,
    val warehouseName: String
): Location(latitude, longitude, address){
    
    fun getinfo(): String{
        return "Warehouse: $warehouseName at $address"
    }
}


//fun main() {
//    val warehouse1 = WareHouseLocation(14.05, 120.40, "123 Warehouse St.", "Main Warehouse")
//    val warehouse2 = WareHouseLocation(14.08, 120.50, "456 Warehouse Ave.", "Secondary Warehouse")
//
//    val distanceMeters = warehouse1.distanceTo(warehouse2)
//    val distanceKm = distanceMeters / 1000.0
//
//    println(warehouse1.getinfo())
//    println("Distance: ${String.format("%.2f", distanceMeters)} meters (${String.format("%.2f", distanceKm)} km)")
//}

