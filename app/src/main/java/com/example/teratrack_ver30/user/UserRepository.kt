package com.example.teratrack_ver30.repository

import com.example.teratrack_ver30.data.UserDao
import com.example.teratrack_ver30.data.UserEntity
import com.example.teratrack_ver30.user.CreateAccount

//Set the Contracts of the Repository
interface UserRepository {
    suspend fun createUser(user: CreateAccount)
    suspend fun getUser(username: String): CreateAccount?
    suspend fun updateUserPassword(username: String, newPassword: String): Boolean
    suspend fun deleteUser(username: String)
}

class RoomUserRepository(private val userDao: UserDao) : UserRepository {

    // CREATE: store the hash and salt, never the plain password
    override suspend fun createUser(user: CreateAccount) {
        val dbUser = UserEntity(
            username = user.username,
            passwordHash = user.passwordHash,
            salt = user.salt,
            favouriteWord = user.favouriteWord
        )
        userDao.save(dbUser)
    }

    // READ: rebuild the account from the stored hash and salt
    override suspend fun getUser(username: String): CreateAccount? {
        val dbUser = userDao.find(username) ?: return null
        val account = CreateAccount(dbUser.username, dbUser.favouriteWord)
        account.restoreHash(dbUser.passwordHash, dbUser.salt)
        return account
    }

    // UPDATE: validate and hash the new password with a new salt, then save
    override suspend fun updateUserPassword(username: String, newPassword: String): Boolean {
        val existingUser = userDao.find(username) ?: return false

        val tempAccount = CreateAccount(username, existingUser.favouriteWord)
        val validationResult = tempAccount.enterPassword(newPassword)

        if (validationResult == "Password set successfully") {
            val updatedEntity = existingUser.copy(
                passwordHash = tempAccount.passwordHash,
                salt = tempAccount.salt
            )
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