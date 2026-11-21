package com.example.teratrack_ver30.repository

import com.example.teratrack_ver30.data.UserDao
import com.example.teratrack_ver30.data.UserEntity
import com.example.teratrack_ver30.user.user.CreateAccount

//Set the Contracts of the Repository
interface UserRepository {
    suspend fun createUser(user: CreateAccount)
    suspend fun getUser(username: String): CreateAccount?
    suspend fun updateUserPassword(username: String, newPassword: String): Boolean
    suspend fun deleteUser(username: String)
}

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    // CREATE
    override suspend fun createUser(user: CreateAccount) {
        val dbUser = UserEntity(
            username = user.username,
            password = user.getPasswordForDb(),
            favouriteWord = user.favouriteWord
        )
        userDao.save(dbUser)
    }

    // READ
    override suspend fun getUser(username: String): CreateAccount? {
        val dbUser = userDao.find(username) ?: return null
        val account = CreateAccount(dbUser.username, dbUser.favouriteWord)
        account.enterPassword(dbUser.password)
        return account
    }

    // First, we search the database for the user. If they don't exist, we return false (meaning the update failed).
    override suspend fun updateUserPassword(username: String, newPassword: String): Boolean {
        val existingUser = userDao.find(username) ?: return false

        // Create a temporary OOP object to validate the new password using your OOP rules
        val tempAccount = CreateAccount(username, existingUser.favouriteWord)
        val validationResult = tempAccount.enterPassword(newPassword)

        if (validationResult == "Password set successfully") {
            // Save the updated entity to Room
            val updatedEntity = existingUser.copy(password = tempAccount.getPasswordForDb())
            userDao.update(updatedEntity)
            return true
        }
        return false // Password was too short
    }

    // DELETE
    override suspend fun deleteUser(username: String) {
        val existingUser = userDao.find(username)
        if (existingUser != null) {
            userDao.delete(existingUser)
        }
    }
}