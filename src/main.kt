import location.Location
import repository.DriverRepository
import repository.TripRepository
import repository.VehicleRepository
import trip.TripService
import trip.TripStatus
import user.Driver
import vehicle.Vehicle
import vehicle.VehicleStatus
import vehicle.VehicleType
import java.time.Instant

var failures = 0

fun check(label: String, ok: Boolean) {
    println((if (ok) "PASS  " else "FAIL  ") + label)
    if (!ok) failures++
}

/** Runs [block] and checks that it throws with a message containing [expected]. */
fun expectError(label: String, expected: String, block: () -> Unit) {
    try {
        block()
        check("$label (expected an error, none thrown)", false)
    } catch (e: Exception) {
        check("$label -> \"${e.message}\"", e.message?.contains(expected) == true)
    }
}

fun main() {
    val tripRepo = TripRepository()
    val vehicleRepo = VehicleRepository()
    val driverRepo = DriverRepository()
    val service = TripService(tripRepo, vehicleRepo, driverRepo)

    val manila = Location(14.5995, 120.9842)
    val dasma = Location(14.3294, 120.9367)

    val truck = vehicleRepo.save(
        Vehicle(plateNumber = "ABC-123", type = VehicleType.TRUCK, currentLocation = manila, capacityKg = 1000.0)
    )
    driverRepo.save(Driver("D-1", "Juan", "Driver", "Manila", "ABC-123", true, true))
    driverRepo.save(Driver("D-2", "Pedro", "Driver", "Cavite", "ABC-123", false, false))

    println("=== 1. Schedule ===")
    val t1 = service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 500.0)
    check("status is SCHEDULED", t1.status == TripStatus.SCHEDULED)
    check("actualStart is null before the trip starts", t1.actualStart == null)
    check("vehicle still IDLE", vehicleRepo.findById(truck.id)!!.status == VehicleStatus.IDLE)

    println("=== 2. Start ===")
    val started = service.startTrip(t1.id)
    check("returned trip is IN_PROGRESS", started.status == TripStatus.IN_PROGRESS)
    check("repository also holds IN_PROGRESS", tripRepo.findById(t1.id)!!.status == TripStatus.IN_PROGRESS)
    check("actualStart is set", started.actualStart != null)
    check("vehicle is IN_TRIP", vehicleRepo.findById(truck.id)!!.status == VehicleStatus.IN_TRIP)
    check("activeTrips() has 1 trip", service.activeTrips().size == 1)
    expectError("starting the same trip twice", "not scheduled") { service.startTrip(t1.id) }

    println("=== 3. Complete ===")
    val done = service.completeTrip(t1.id)
    check("returned trip is COMPLETED", done.status == TripStatus.COMPLETED)
    check("repository also holds COMPLETED", tripRepo.findById(t1.id)!!.status == TripStatus.COMPLETED)
    check("actualEnd is set", done.actualEnd != null)
    check("durationSeconds is calculated", done.durationSeconds != null)
    check("vehicle back to IDLE", vehicleRepo.findById(truck.id)!!.status == VehicleStatus.IDLE)
    check("activeTrips() is empty", service.activeTrips().isEmpty())
    expectError("completing a finished trip", "not in progress") { service.completeTrip(t1.id) }
    expectError("cancelling a finished trip", "cannot be cancelled") { service.cancelTrip(t1.id) }

    println("=== 4. Cancel a SCHEDULED trip ===")
    val t2 = service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 200.0)
    val c2 = service.cancelTrip(t2.id)
    check("returned trip is CANCELLED", c2.status == TripStatus.CANCELLED)
    check("repository also holds CANCELLED", tripRepo.findById(t2.id)!!.status == TripStatus.CANCELLED)
    check("vehicle still IDLE", vehicleRepo.findById(truck.id)!!.status == VehicleStatus.IDLE)
    check("cancelled trip is not in activeTrips()", service.activeTrips().isEmpty())

    println("=== 5. Cancel an IN_PROGRESS trip ===")
    val t3 = service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 300.0)
    service.startTrip(t3.id)
    check("vehicle IN_TRIP while running", vehicleRepo.findById(truck.id)!!.status == VehicleStatus.IN_TRIP)
    val c3 = service.cancelTrip(t3.id)
    check("returned trip is CANCELLED", c3.status == TripStatus.CANCELLED)
    check("vehicle freed back to IDLE", vehicleRepo.findById(truck.id)!!.status == VehicleStatus.IDLE)
    check("activeTrips() is empty", service.activeTrips().isEmpty())

    println("=== 6. Validation rules ===")
    expectError("cargo over capacity", "exceeds") {
        service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 5000.0)
    }
    expectError("driver off duty", "not on duty") {
        service.scheduleTrip(truck.id, "D-2", manila, dasma, Instant.now(), 100.0)
    }
    expectError("unknown vehicle", "Vehicle not found") {
        service.scheduleTrip("nope", "D-1", manila, dasma, Instant.now(), 100.0)
    }
    expectError("unknown driver", "Driver not found") {
        service.scheduleTrip(truck.id, "nope", manila, dasma, Instant.now(), 100.0)
    }
    expectError("unknown trip", "Trip not found") { service.startTrip("nope") }

    println("=== 7. Vehicle busy ===")
    val a = service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 100.0)
    val b = service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 100.0)
    service.startTrip(a.id)
    expectError("starting a 2nd trip on a busy vehicle", "not available") { service.startTrip(b.id) }
    expectError("scheduling on a busy vehicle", "not available") {
        service.scheduleTrip(truck.id, "D-1", manila, dasma, Instant.now(), 100.0)
    }

    println()
    println(if (failures == 0) "ALL CHECKS PASSED" else "$failures CHECK(S) FAILED")
}