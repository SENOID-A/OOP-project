package user

import com.example.teratrack_ver30.user.user.CreateAccount


// 1. The Contract (Interface)
// This allows us to switch from Memory to SQLite without breaking the app.
interface UserRepository {
    fun saveUser(user: CreateAccount)
    fun findUser(username: String): CreateAccount?
}

// 2. The In-Memory Implementation (For testing right now)
class InMemoryUserRepository : UserRepository {
    // This list acts as our temporary "database"
    private val users = mutableListOf<CreateAccount>()

    override fun saveUser(user: CreateAccount) {
        // Remove existing user with the same name, then add the new one
        users.removeAll { it.username == user.username }
        users.add(user)
    }

    override fun findUser(username: String): CreateAccount? {
        return users.find { it.username == username }
    }
}