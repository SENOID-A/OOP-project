package repository
import user.Driver

class DriverRepository {
    private val drivers = mutableMapOf<String, Driver>()

    fun save(d: Driver): Driver {
        drivers[d.staffId] = d
        return d
    }

    fun findById(id: String): Driver? = drivers[id]

    fun all(): List<Driver> = drivers.values.toList()
}