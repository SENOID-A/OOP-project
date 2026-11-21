package com.example.teratrack_ver30.user.user

import com.example.teratrack_ver30.user.User

class CreateAccount (
    username: String,
    val favouriteWord: String
): User(username) {
    var password: String = ""
        private set

    fun getPasswordForDb(): String = password

    override fun enterPassword(password: String): String {
        if (password.length >= 8) {
            this.password = password
            return "Password set successfully"
        }
        else {
            return "Password must be at least 8 characters long"
        }
    }
    fun verifyPassword(inputPassword: String): Boolean{
        return this.password == inputPassword

    }

}