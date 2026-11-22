package com.example.teratrack_ver30.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teratrack_ver30.data.DriverEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDriverScreen(
    navController: NavController,
    driverVm: DriverViewModel,
    vehicleVm: VehicleViewModel
) {
    val vehicles by vehicleVm.vehicles.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // CONDITION: You can only add a driver if there is at least one vehicle.
    if (vehicles.isEmpty()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color(0xFF0D1B3E) // <-- CHANGED TO NAVY BLUE
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Add Driver", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                HorizontalDivider(color = Color.Gray)
                Text(
                    "You must add at least one vehicle before adding a driver.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 24.dp)
                )
            }
        }
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B3E) // <-- CHANGED TO NAVY BLUE
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Add Driver", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            HorizontalDivider(color = Color.Gray)

            // Text Fields (White background, Black text)
            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            // Save Button (Unchanged)
            Button(
                onClick = {
                    if (name.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Name cannot be empty") }
                        return@Button
                    }
                    if (phone.length < 7) {
                        scope.launch { snackbarHostState.showSnackbar("Phone number must be valid") }
                        return@Button
                    }
                    driverVm.add(DriverEntity(name = name, address = address, phone = phone))
                    scope.launch { snackbarHostState.showSnackbar("Driver added!") }
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)) // Orange button
            ) {
                Text(
                    text = "Add Driver",
                    color = Color.White
                )
            }
        }
    }
}