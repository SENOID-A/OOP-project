package com.example.teratrack_ver30.user

import com.example.teratrack_ver30.user.CreateAccount

class ForgotPassword(private val account: CreateAccount) {
    fun getSecurityQuestion(): String {
        return "What is your favourite word?"
    }

    suspend fun updateUserPassword(answerAttempt: String, newPassword: String): String{
        if (answerAttempt.equals(account.favouriteWord, ignoreCase = true)){
            val result = account.enterPassword(newPassword)
            return if (result == "Password set successfully") {
                "Password reset successful!"
            }
            else {
                result
            }
        }
        else {
            return "Incorrect answer. Password reset failed."
        }

    }
}