package com.example.teratrack_ver30

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.teratrack_ver30.data.AppDatabase
import com.example.teratrack_ver30.repository.RoomUserRepository
import com.example.teratrack_ver30.repository.UserRepository
import com.example.teratrack_ver30.ui.theme.Teratrack_ver30Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // 1. Declare the repository
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Initialize the Database and Repository
        val db = AppDatabase.getDatabase(this)
        userRepository = RoomUserRepository(db.userDao())

        setContent {
            Teratrack_ver30Theme {
                AppNavigation(userRepository)
            }
        }
    }

    // 4. The Room DB Logic for Login
    private fun handleLogin(username: String, password: String) {
        // Validation: Make sure fields aren't empty
        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Use lifecycleScope to run the suspend database functions
        lifecycleScope.launch {
            // Fetch user from Room DB
            val userAccount = userRepository.getUser(username)

            if (userAccount != null) {
                // Use your OOP method to verify the password!
                if (userAccount.verifyPassword(password)) {
                    Toast.makeText(this@MainActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    // TODO: Navigate to Home/Dashboard Screen here
                } else {
                    Toast.makeText(this@MainActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}