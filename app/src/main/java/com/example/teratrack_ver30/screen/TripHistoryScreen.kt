package com.example.teratrack_ver30.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teratrack_ver30.viewModel.TripViewModel
import com.example.teratrack_ver30.viewModel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripHistoryScreen(
    navController: NavController,
    tripVm: TripViewModel,
    vehicleVm: VehicleViewModel
) {
    val trips by tripVm.trips.collectAsState()
    val vehicles by vehicleVm.vehicles.collectAsState()
    var showDialogForId by remember { mutableStateOf<Int?>(null) }
    var showDeleteDialogForId by remember { mutableStateOf<Int?>(null) }
    val backgroundGray = Color(0xFFD9D9D9)

    Column(Modifier.fillMaxSize().background(backgroundGray).padding(16.dp).padding(top = 32.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    // ADDED: Return directly to Dashboard
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back to Dashboard")
            }
            Text("Trip History", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        LazyColumn {
            items(trips) { trip ->
                val vehicle = vehicles.firstOrNull { it.id == trip.vehicleId }

                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Vehicle ID: ${vehicle?.vehicleId ?: "Unknown"}", fontWeight = FontWeight.Bold)
                            Text("Date: ${trip.date}")
                            Text("Driver: ${trip.driverName}")
                            Text("Description: ${trip.description}")
                            Text("Destination: ${trip.destination}")
                            Text(
                                "Status: ${trip.status}",
                                color = if (trip.status == "Completed") Color.Green else Color.Red,
                                fontWeight = FontWeight.Bold
                            )

                            if (trip.status == "Pending") {
                                Button(
                                    onClick = { showDialogForId = trip.id },
                                    modifier = Modifier.padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                                ) {
                                    Text("Mark as Completed", color = Color.White)
                                }
                            }
                        }
                        IconButton(onClick = { showDeleteDialogForId = trip.id }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }

                if (showDialogForId == trip.id) {
                    AlertDialog(
                        onDismissRequest = { showDialogForId = null },
                        title = { Text("Confirm") },
                        text = { Text("Is the trip completed?") },
                        confirmButton = {
                            TextButton(onClick = {
                                tripVm.update(trip.copy(status = "Completed"))
                                showDialogForId = null
                            }) { Text("Yes") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialogForId = null }) { Text("No") }
                        }
                    )
                }

                if (showDeleteDialogForId == trip.id) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialogForId = null },
                        title = { Text("Confirm") },
                        text = { Text("Are you sure you want to delete this trip?") },
                        confirmButton = {
                            TextButton(onClick = {
                                tripVm.delete(trip)
                                showDeleteDialogForId = null
                            }) { Text("Yes") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialogForId = null }) { Text("No") }
                        }
                    )
                }
            }
        }
    }
}