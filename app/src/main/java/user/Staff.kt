package user

abstract class Staff(
    val staffId: String,
    val name: String,
    val position: String,
    val address: String
){
    abstract fun getInfo(): String
}

