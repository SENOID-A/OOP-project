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
import com.example.teratrack_ver30.ui.theme.VehicleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleListScreen(navController: NavController, vm: VehicleViewModel) {
    val vehicles by vm.vehicles.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp).padding(top = 32.dp)) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                        // Display Image if it exists
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

                        // Text Details
                        Column(Modifier.weight(1f)) {
                            Text("Vehicle ID: ${vehicle.vehicleId}", fontWeight = FontWeight.Bold)
                            Text("Driver: ${vehicle.driverName ?: "Unassigned"}")
                        }

                        // Delete Button
                        IconButton(onClick = { vm.delete(vehicle) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}