package com.example.teratrack_ver30.user

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHasher {

    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((salt + password).toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun matches(input: String, salt: String, storedHash: String): Boolean =
        MessageDigest.isEqual(
            hash(input, salt).toByteArray(),
            storedHash.toByteArray()
        )
}