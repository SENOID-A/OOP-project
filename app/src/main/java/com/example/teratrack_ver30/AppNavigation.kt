package com.example.teratrack_ver30

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.teratrack_ver30.repository.UserRepository
import com.example.teratrack_ver30.data.AppContainer
import kotlinx.coroutines.launch

// UI & ViewModels (Make sure your packages match)

import com.example.teratrack_ver30.ui.VehicleListScreen

@Composable
fun AppNavigation(userRepository: UserRepository) {

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // 1. Setup Container & ViewModels
    val container = remember { AppContainer(context) }

    val vehicleVm: VehicleViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return VehicleViewModel(container.vehicleRepository) as T
            }
        }
    )

    val driverVm: DriverViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return DriverViewModel(container.driverRepository) as T
            }
        }
    )

    val maintenanceVm: MaintenanceViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MaintenanceViewModel(container.maintenanceRepository) as T
            }
        }
    )

    val tripVm: TripViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TripViewModel(container.tripRepository, container.warehouseRepository) as T
            }
        }
    )

    // 2. Navigation
    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = Modifier
    ) {

        composable("login") {
            LoginScreen(
                onLoginClick = { username, password ->
                    if (username.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter username and password", Toast.LENGTH_SHORT).show()
                    } else {
                        coroutineScope.launch {
                            val userAccount = userRepository.getUser(username)
                            if (userAccount != null && userAccount.verifyPassword(password)) {
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                navController.navigate("dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onCreateAccountClick = { navController.navigate("create_account") }
            )
        }

        composable("create_account") {
            CreateAccScreen(
                userRepository = userRepository,
                onAccountCreated = { navController.popBackStack() },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable("forgot_password") {
            ForgotPasswordScreen(
                userRepository = userRepository,
                onPasswordReset = { navController.popBackStack("login", false) },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // DASHBOARD
        composable("dashboard") {
            DashboardScreen(
                onNavigateToVehicles = { navController.navigate("vehicle_list") },
                onNavigateToDrivers = { navController.navigate("driver_list") },
                onNavigateToAddVehicle = { navController.navigate("add_vehicle") },
                onNavigateToAddDriver = { navController.navigate("add_driver") },
                onNavigateToMaintenance = { navController.navigate("maintenance_history") },
                onNavigateToScheduleMaintenance = { navController.navigate("schedule_maintenance") },
                onNavigateToTrips = { navController.navigate("trip_history") },
                onNavigateToScheduleTrip = { navController.navigate("schedule_trip") },
                onNavigateToMap = { navController.navigate("map") } // <-- ADDED
            )
        }

        // VEHICLES
        composable("vehicle_list") { VehicleListScreen(navController, vehicleVm) }
        composable("add_vehicle") { AddVehicleScreen(navController, vehicleVm) }

        // DRIVERS
        composable("driver_list") { DriverListScreen(navController, driverVm, vehicleVm) }
        composable("add_driver") { AddDriverScreen(navController, driverVm, vehicleVm) }

        // MAINTENANCE
        composable("maintenance_history") { MaintenanceHistoryScreen(navController, maintenanceVm, vehicleVm) }
        composable("schedule_maintenance") { ScheduleMaintenanceScreen(navController, maintenanceVm, vehicleVm) }

        // TRIPS
        composable("trip_history") { TripHistoryScreen(navController, tripVm, vehicleVm) }
        composable("schedule_trip") { ScheduleTripScreen(navController, tripVm, vehicleVm, driverVm) }

        // MAP (ADDED)
        composable("map") { MapScreen(navController, tripVm, vehicleVm) }
    }
}