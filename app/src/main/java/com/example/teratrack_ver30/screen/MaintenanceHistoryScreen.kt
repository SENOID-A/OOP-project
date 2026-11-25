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
import com.example.teratrack_ver30.viewModel.MaintenanceViewModel
import com.example.teratrack_ver30.viewModel.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceHistoryScreen(
    navController: NavController,
    maintenanceVm: MaintenanceViewModel,
    vehicleVm: VehicleViewModel
) {
    val records by maintenanceVm.maintenanceRecords.collectAsState()
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
            Text("Maintenance History", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        LazyColumn {
            items(records) { record ->
                val vehicle = vehicles.firstOrNull { it.id == record.vehicleId }

                Card(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Vehicle ID: ${vehicle?.vehicleId ?: "Unknown"}", fontWeight = FontWeight.Bold)
                            Text("Date: ${record.date}")
                            Text("Problem: ${record.problem}")
                            Text(
                                "Status: ${record.status}",
                                color = if (record.status == "Completed") Color.Green else Color.Red,
                                fontWeight = FontWeight.Bold
                            )

                            if (record.status == "Pending") {
                                Button(
                                    onClick = { showDialogForId = record.id },
                                    modifier = Modifier.padding(top = 8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                                ) {
                                    Text("Mark as Completed", color = Color.White)
                                }
                            }
                        }
                        IconButton(onClick = { showDeleteDialogForId = record.id }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }

                if (showDialogForId == record.id) {
                    AlertDialog(
                        onDismissRequest = { showDialogForId = null },
                        title = { Text("Confirm") },
                        text = { Text("Is the maintenance completed?") },
                        confirmButton = {
                            TextButton(onClick = {
                                maintenanceVm.update(record.copy(status = "Completed"))
                                showDialogForId = null
                            }) { Text("Yes") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDialogForId = null }) { Text("No") }
                        }
                    )
                }

                if (showDeleteDialogForId == record.id) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialogForId = null },
                        title = { Text("Confirm") },
                        text = { Text("Are you sure you want to delete this maintenance record?") },
                        confirmButton = {
                            TextButton(onClick = {
                                maintenanceVm.delete(record)
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