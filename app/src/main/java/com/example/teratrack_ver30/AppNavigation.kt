
package com.example.teratrack_ver30

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teratrack_ver30.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(userRepository: UserRepository) {

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier

    ) {

        composable("login") {

            LoginScreen(
                onLoginClick = { username, password ->

                    // Check empty fields first
                    if (username.isBlank() || password.isBlank()) {

                        Toast.makeText(
                            context,
                            "Please enter username and password",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        coroutineScope.launch {

                            // Find the user in the database
                            val userAccount =
                                userRepository.getUser(username)

                            if (userAccount != null &&
                                userAccount.verifyPassword(password)
                            ) {

                                Toast.makeText(
                                    context,
                                    "Login Successful!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                navController.navigate("dashboard") {
                                    popUpTo("login") {
                                        inclusive = true
                                    }
                                }

                            } else {

                                Toast.makeText(
                                    context,
                                    "Invalid username or password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                },

                onForgotPasswordClick = {
                    navController.navigate("forgot_password")
                },

                onCreateAccountClick = {
                    navController.navigate("create_account")
                }
            )
        }

        composable("create_account") {
            CreateAccScreen(
                userRepository = userRepository,
                onAccountCreated = {
                    navController.popBackStack()
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("forgot_password") {

            ForgotPasswordScreen(
                userRepository = userRepository,

                onPasswordReset = {
                    navController.popBackStack("login", false)
                },

                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("dashboard") {
            DashboardScreen(
                onNavigateToVehicles = {},
                onNavigateToDrivers = {},
                onNavigateToMaintenance = {},
                onNavigateToTrips = {}
            )
        }
    }
}
