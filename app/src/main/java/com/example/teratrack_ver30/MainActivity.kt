package com.example.teratrack_ver30

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.teratrack_ver30.data.UserDatabase
import com.example.teratrack_ver30.dataEntry.AppNavigation
import com.example.teratrack_ver30.repository.RoomUserRepository
import com.example.teratrack_ver30.repository.UserRepository
import com.example.teratrack_ver30.ui.theme.Teratrack_ver30Theme

class MainActivity : ComponentActivity() {

    // 1. Declare the repository
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 2. Initialize the Database and Repository
        val db = UserDatabase.getDatabase(this)
        userRepository = RoomUserRepository(db.userDao())

        setContent {
            Teratrack_ver30Theme {
                AppNavigation(userRepository)
            }
        }
    }

    // 4. The Room DB Logic for Login

}