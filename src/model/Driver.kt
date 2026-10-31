package model

data class Driver(
    val name: String,
    val driverId: String,
    val licenseNum: String,
    val password: String,
    var isAvailable: Boolean
) {
    
    fun openAccount(){

    }
    
    fun getInfor(): String{
        return "Name: $name, " +
                "Driver ID: $driverId, " +
                "License Number: $licenseNum"
    }

    fun assignToTrips(){

    }

    fun comleteTrip(){
        
    }

}