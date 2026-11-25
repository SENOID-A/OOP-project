package com.example.teratrack_ver30.user
import com.example.teratrack_ver30.user.PasswordHasher
import com.example.teratrack_ver30.user.User

class CreateAccount(
    username: String,
    val favouriteWord: String
) : User(username) {

    var passwordHash: String = ""
        private set
    var salt: String = ""
        private set

    override fun enterPassword(password: String): String {
        if (password.length < 8) {
            return "Password must be at least 8 characters long"
        }
        salt = PasswordHasher.generateSalt()
        passwordHash = PasswordHasher.hash(password, salt)
        return "Password set successfully"
    }

    fun verifyPassword(inputPassword: String): Boolean =
        PasswordHasher.matches(inputPassword, salt, passwordHash)

    // Used by the repository to rebuild an account from the database
    fun restoreHash(hash: String, salt: String) {
        this.passwordHash = hash
        this.salt = salt
    }
}