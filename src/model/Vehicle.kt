package model
import location.Location

//Abstract Super Class
abstract class Vehicle(
    val vin: Vin,
    var location: Location? = null,
    var driver: Driver? = null
) {
    var isAssigned: Boolean = false

    abstract fun getCapacity(): Double

    open fun getInfo(): String {
        return "VIN: ${vin.number}, " +
               "Location: ${location.address}, " +
               "Assigned: $isAssigned"
    }

    fun updateLocation(location: Location) {
        this.location = location
    }
}

class Truck(
    vin: Vin,
    location: Location,
    var payloadCapacity: Double,
    var numberOfAxles: Int
) : Vehicle(vin, location) {
    override fun getCapacity(): Double {
        return payloadCapacity
    }

    override fun getInfo(): String {
        return super.getInfo() + ", " +
               "Payload Capacity: $payloadCapacity, " +
               "Number of Axles: $numberOfAxles"
    }
}

fun main(){
    val truck1 = Truck(Vin("1HGCM82633A123456"), Location("123 Main St"), 5000.0, 4)
    println(truck1.getCapacity())
    println(truck1.getInfo())
}