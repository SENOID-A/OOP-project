package user

class FleetManager(
    staffId: String,
    name: String,
    position: String,
    address: String
): Staff(staffId, name, position, address){

    private var password: String = "defaultPassword"
        set(value) {
            require(value.length >= 8) { "Password must be at least 8 characters" }
            field = value
        }

    fun changePassword(old: String, new: String) {
        require(old == password) { "Current password is incorrect" } //Checking
        password = new    // runs the private setter → validation applies
    }

    override fun getInfo(): String{
        return "Staff ID: $staffId\n" +
                "Name: $name\n" +
                "Position: $position\n" +
                "Address: $address"
    }
}
