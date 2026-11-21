package com.example.teratrack_ver30

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teratrack_ver30.repository.UserRepository

@Composable
fun AppNavigation(userRepository: UserRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        
        // 1. Login Screen
        composable("login") {
            LoginScreen(
                onLoginClick = { username, password ->
                    // TODO: Add real authentication here
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true } // Don't go back to login on back press
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                },
                onCreateAccountClick = { // You will need to add this parameter to LoginScreen
                    navController.navigate("create_account")
                }
            )
        }
        composable("create_account") {
            CreateAccScreen(
                userRepository = userRepository, // <-- Pass the repository here
                onAccountCreated = {
                    // After successful registration, pop back to login
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 2. Forgot Password Screen (Placeholder)
        composable("forgot_password") {
            // We will build this UI next
            Text("Forgot Password Screen")
        }

        // 3. Create Account Screen (Placeholder)
        composable("create_account") {
            // We will build this UI next
            Text("Create Account Screen")
        }

        // 4. Dashboard (The Screenshot)
        composable("dashboard") {
            DashboardScreen(
                onNavigateToVehicles = { /* TODO */ },
                onNavigateToDrivers = { /* TODO */ },
                onNavigateToMaintenance = { /* TODO */ },
                onNavigateToTrips = { /* TODO */ }
            )
        }
    }
}