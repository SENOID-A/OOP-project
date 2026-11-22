package com.example.teratrack_ver30.screen

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teratrack_ver30.viewModel.DriverViewModel
import com.example.teratrack_ver30.viewModel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverListScreen(
    navController: NavController,
    driverVm: DriverViewModel,
    vehicleVm: VehicleViewModel
) {
    val drivers by driverVm.drivers.collectAsState()
    val unassigned by vehicleVm.unassigned.collectAsState()
    val allVehicles by vehicleVm.vehicles.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp).padding(top = 32.dp)) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Drivers", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        LazyColumn {
            items(drivers) { driver ->
                // Find if this driver has a vehicle
                val assignedVehicle = allVehicles.firstOrNull { it.id == driver.vehicleId }

                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                "Vehicle ID: ${assignedVehicle?.vehicleId ?: "Unassigned"}",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("Name: ${driver.name}")
                            Text("Address: ${driver.address}")
                            Text("Phone: ${driver.phone}")

                            // The Dropdown for assigning a vehicle
                            var expanded by remember { mutableStateOf(false) }
                            Box(Modifier.padding(top = 8.dp)) {
                                Button(onClick = { expanded = true }) {
                                    Text("Assign Vehicle ▾")
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    if (unassigned.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No unassigned vehicles") },
                                            onClick = { expanded = false }
                                        )
                                    } else {
                                        unassigned.forEach { v ->
                                            DropdownMenuItem(
                                                text = { Text(v.vehicleId) },
                                                onClick = {
                                                    // Update both tables!
                                                    vehicleVm.update(v.copy(driverName = driver.name))
                                                    driverVm.update(driver.copy(vehicleId = v.id))
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Delete Button
                        IconButton(onClick = { driverVm.delete(driver) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}