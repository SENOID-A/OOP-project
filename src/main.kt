import location.Location
import maintenance.MaintenanceService
import maintenance.MaintenanceStatus
import repository.DriverRepository
import repository.MaintenanceRepository
import repository.TrackingRepository
import repository.TripRepository
import repository.VehicleRepository
import tracking.TrackingService
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
    // ---------- Setup: repositories and services ----------
    val tripRepo = TripRepository()
    val vehicleRepo = VehicleRepository()
    val driverRepo = DriverRepository()
    val maintenanceRepo = MaintenanceRepository()
    val trackingRepo = TrackingRepository()

    val service = TripService(tripRepo, vehicleRepo, driverRepo)
    val maintenanceService = MaintenanceService(maintenanceRepo, vehicleRepo)
    val trackingService = TrackingService(trackingRepo, vehicleRepo, tripRepo)

    val manila = Location(14.5995, 120.9842)
    val dasma = Location(14.3294, 120.9367)

    val truck = vehicleRepo.save(
        Vehicle(plateNumber = "ABC-123", type = VehicleType.TRUCK, currentLocation = manila, capacityKg = 1000.0)
    )
    driverRepo.save(Driver("D-1", "Juan", "Driver", "Manila", "ABC-123", true, true))
    driverRepo.save(Driver("D-2", "Pedro", "Driver", "Cavite", "ABC-123", false, false))

    // =====================================================================
    //  TRIP SERVICE
    // =====================================================================

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

    // =====================================================================
    //  MAINTENANCE SERVICE  (uses its own vehicles so the trip tests above don't interfere)
    // =====================================================================

    println("=== 8. Maintenance: schedule ===")
    val van = vehicleRepo.save(
        Vehicle(plateNumber = "MNT-001", type = VehicleType.VAN, currentLocation = manila)
    )
    val m1 = maintenanceService.scheduleMaintenance(van.id, "Oil change", Instant.now())
    check("status is SCHEDULED", m1.status == MaintenanceStatus.SCHEDULED)
    check("startedAt is null before start", m1.startedAt == null)
    check("repository holds the record", maintenanceRepo.findById(m1.id) != null)
    check("vehicle still IDLE after scheduling", vehicleRepo.findById(van.id)!!.status == VehicleStatus.IDLE)
    expectError("blank description", "cannot be blank") {
        maintenanceService.scheduleMaintenance(van.id, "   ", Instant.now())
    }
    expectError("second pending record on same vehicle", "already has pending") {
        maintenanceService.scheduleMaintenance(van.id, "Tire rotation", Instant.now())
    }
    expectError("unknown vehicle", "Vehicle not found") {
        maintenanceService.scheduleMaintenance("nope", "Oil change", Instant.now())
    }

    println("=== 9. Maintenance: start ===")
    val s1 = maintenanceService.startMaintenance(m1.id)
    check("record is IN_PROGRESS", s1.status == MaintenanceStatus.IN_PROGRESS)
    check("startedAt is set", s1.startedAt != null)
    check("vehicle is MAINTENANCE", vehicleRepo.findById(van.id)!!.status == VehicleStatus.MAINTENANCE)
    check("activeMaintenance() has 1 record", maintenanceService.activeMaintenance().size == 1)
    check("vehicle is not available while in maintenance", !vehicleRepo.findById(van.id)!!.isAvailable)
    expectError("starting the same record twice", "not scheduled") { maintenanceService.startMaintenance(m1.id) }
    expectError("scheduling on a vehicle in maintenance", "must be idle") {
        maintenanceService.scheduleMaintenance(van.id, "Brake check", Instant.now())
    }
    expectError("unknown record", "Maintenance record not found") { maintenanceService.startMaintenance("nope") }

    println("=== 10. Maintenance: complete ===")
    expectError("negative cost", "Cost cannot be negative") { maintenanceService.completeMaintenance(m1.id, -5.0) }
    val d1 = maintenanceService.completeMaintenance(m1.id, 1500.0)
    check("record is COMPLETED", d1.status == MaintenanceStatus.COMPLETED)
    check("completedAt is set", d1.completedAt != null)
    check("cost is stored", d1.cost == 1500.0)
    check("vehicle back to IDLE", vehicleRepo.findById(van.id)!!.status == VehicleStatus.IDLE)
    check("activeMaintenance() is empty", maintenanceService.activeMaintenance().isEmpty())
    expectError("completing a finished record", "not in progress") { maintenanceService.completeMaintenance(m1.id, 100.0) }
    expectError("cancelling a finished record", "cannot be cancelled") { maintenanceService.cancelMaintenance(m1.id) }

    println("=== 11. Maintenance: cancel a SCHEDULED record ===")
    val m2 = maintenanceService.scheduleMaintenance(van.id, "Tire rotation", Instant.now())
    val c4 = maintenanceService.cancelMaintenance(m2.id)
    check("record is CANCELLED", c4.status == MaintenanceStatus.CANCELLED)
    check("completedAt stays null for a cancelled record", c4.completedAt == null)
    check("vehicle still IDLE", vehicleRepo.findById(van.id)!!.status == VehicleStatus.IDLE)

    println("=== 12. Maintenance: cancel an IN_PROGRESS record ===")
    val m3 = maintenanceService.scheduleMaintenance(van.id, "Brake check", Instant.now())
    maintenanceService.startMaintenance(m3.id)
    check("vehicle is MAINTENANCE while running", vehicleRepo.findById(van.id)!!.status == VehicleStatus.MAINTENANCE)
    val c5 = maintenanceService.cancelMaintenance(m3.id)
    check("record is CANCELLED", c5.status == MaintenanceStatus.CANCELLED)
    check("completedAt stays null", c5.completedAt == null)
    check("vehicle freed back to IDLE", vehicleRepo.findById(van.id)!!.status == VehicleStatus.IDLE)
    check("activeMaintenance() is empty", maintenanceService.activeMaintenance().isEmpty())
    check("forVehicle() returns all 3 records", maintenanceRepo.forVehicle(van.id).size == 3)

    println("=== 13. Maintenance: cannot start on a vehicle that is on a trip ===")
    val van2 = vehicleRepo.save(
        Vehicle(plateNumber = "MNT-002", type = VehicleType.VAN, currentLocation = manila)
    )
    val m4 = maintenanceService.scheduleMaintenance(van2.id, "Inspection", Instant.now())
    val tripOnVan2 = service.scheduleTrip(van2.id, "D-1", manila, dasma, Instant.now(), 100.0)
    service.startTrip(tripOnVan2.id)
    check("van2 is IN_TRIP", vehicleRepo.findById(van2.id)!!.status == VehicleStatus.IN_TRIP)
    expectError("starting maintenance on an IN_TRIP vehicle", "must be idle") {
        maintenanceService.startMaintenance(m4.id)
    }
    check("van2 is still IN_TRIP (not overwritten)", vehicleRepo.findById(van2.id)!!.status == VehicleStatus.IN_TRIP)

    // =====================================================================
    //  TRACKING SERVICE
    // =====================================================================

    println("=== 14. Tracking: update location during a trip ===")
    val trk = vehicleRepo.save(
        Vehicle(plateNumber = "TRK-001", type = VehicleType.TRUCK, currentLocation = manila)
    )
    val tripT = service.scheduleTrip(trk.id, "D-1", manila, dasma, Instant.now(), 100.0)
    service.startTrip(tripT.id)

    val midway = Location(14.4645, 120.9605)
    val ping1 = trackingService.updateLocation(trk.id, midway)
    check("ping stores the new location", ping1.location == midway)
    check("currentLocation is updated", trackingService.currentLocation(trk.id) == midway)
    check("odometer increased", vehicleRepo.findById(trk.id)!!.odometerKm > 0.0)
    check("trip actualDistanceKm increased", tripRepo.findById(tripT.id)!!.actualDistanceKm > 0.0)
    check("history has 1 ping", trackingService.history(trk.id).size == 1)
    check("trip is not at destination yet", !trackingService.hasArrived(tripT.id))
    check("distance to destination is positive", trackingService.distanceToDestinationKm(tripT.id) > 0.0)

    println("=== 15. Tracking: arrival ===")
    trackingService.updateLocation(trk.id, dasma)
    check("history has 2 pings", trackingService.history(trk.id).size == 2)
    check("latest ping is the destination", trackingRepo.latestForVehicle(trk.id)!!.location == dasma)
    check("hasArrived is true at the destination", trackingService.hasArrived(tripT.id))
    check("distance to destination is ~0", trackingService.distanceToDestinationKm(tripT.id) < 0.01)

    println("=== 16. Tracking: distance is not added after the trip ends ===")
    service.completeTrip(tripT.id)
    val distanceAtEnd = tripRepo.findById(tripT.id)!!.actualDistanceKm
    trackingService.updateLocation(trk.id, manila)
    check("completed trip distance unchanged", tripRepo.findById(tripT.id)!!.actualDistanceKm == distanceAtEnd)
    check("vehicle odometer still increases", vehicleRepo.findById(trk.id)!!.odometerKm > distanceAtEnd)

    println("=== 17. Tracking: validation rules ===")
    val offlineVan = vehicleRepo.save(
        Vehicle(
            plateNumber = "TRK-002",
            type = VehicleType.VAN,
            status = VehicleStatus.OFFLINE,
            currentLocation = manila,
        )
    )
    expectError("offline vehicle cannot report a location", "offline") {
        trackingService.updateLocation(offlineVan.id, dasma)
    }
    expectError("unknown vehicle (update)", "Vehicle not found") {
        trackingService.updateLocation("nope", dasma)
    }
    expectError("unknown vehicle (current location)", "Vehicle not found") {
        trackingService.currentLocation("nope")
    }
    expectError("unknown trip", "Trip not found") { trackingService.distanceToDestinationKm("nope") }

    println("=== 18. Tracking: vehicles near a location ===")
    vehicleRepo.save(Vehicle(plateNumber = "TRK-003", type = VehicleType.VAN, currentLocation = dasma))
    vehicleRepo.save(
        Vehicle(
            plateNumber = "TRK-004",
            type = VehicleType.VAN,
            status = VehicleStatus.IN_TRIP,
            currentLocation = manila,
        )
    )
    val nearManila = trackingService.vehiclesNear(manila, 5.0)
    check("TRK-001 is near Manila", nearManila.any { it.plateNumber == "TRK-001" })
    check("TRK-003 (in Dasma) is not near Manila", nearManila.none { it.plateNumber == "TRK-003" })
    check("TRK-004 (busy) is near Manila", nearManila.any { it.plateNumber == "TRK-004" })

    val freeNearManila = trackingService.availableVehiclesNear(manila, 5.0)
    check("available list includes idle TRK-001", freeNearManila.any { it.plateNumber == "TRK-001" })
    check("available list excludes busy TRK-004", freeNearManila.none { it.plateNumber == "TRK-004" })

    println()
    println(if (failures == 0) "ALL CHECKS PASSED" else "$failures CHECK(S) FAILED")
}