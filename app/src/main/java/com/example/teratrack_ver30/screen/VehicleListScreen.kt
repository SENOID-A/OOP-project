
package com.example.teratrack_ver30.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teratrack_ver30.viewModel.VehicleViewModel
import androidx.compose.runtime.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(navController: NavController, vm: VehicleViewModel) {
    val vehicles by vm.vehicles.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp).padding(top = 32.dp)) {
        // Header
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
            Text("Vehicles", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        // List of Vehicles
        LazyColumn {
            items(vehicles) { vehicle ->
                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        vehicle.imagePath?.let { path ->
                            val bmp = BitmapFactory.decodeFile(path)
                            if (bmp != null) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Vehicle Image",
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                        }

                        Column(Modifier.weight(1f)) {
                            Text("Vehicle ID: ${vehicle.vehicleId}", fontWeight = FontWeight.Bold)
                            Text("Driver: ${vehicle.driverName ?: "Unassigned"}")
                        }

//                        IconButton(onClick = { vm.delete(vehicle) }) {
//                            Icon(Icons.Default.Delete, contentDescription = "Delete")
//                        }



                        var showDeleteDialogForId by remember { mutableStateOf<Int?>(null) }

                        IconButton(onClick = { showDeleteDialogForId = vehicle.id }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }

                        // after the Card, inside items(vehicles) { vehicle -> ... }
                        if (showDeleteDialogForId == vehicle.id) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialogForId = null },
                                title = { Text("Confirm") },
                                text = { Text("Delete vehicle ${vehicle.vehicleId}? Its trips and maintenance records will also be deleted.") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        vm.delete(vehicle)
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
    }
}
