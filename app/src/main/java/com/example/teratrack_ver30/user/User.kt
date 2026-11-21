package com.example.teratrack_ver30.user

abstract class User(val username: String){
    abstract fun enterPassword(password: String): String

}
