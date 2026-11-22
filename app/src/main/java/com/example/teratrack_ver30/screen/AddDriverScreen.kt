package com.example.teratrack_ver30.screen

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
import com.example.teratrack_ver30.viewModel.DriverViewModel
import com.example.teratrack_ver30.viewModel.VehicleViewModel
import com.example.teratrack_ver30.data.DriverEntity
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDriverScreen(
    navController: NavController,
    driverVm: DriverViewModel,
    vehicleVm: VehicleViewModel // not required anymore; kept so the existing navigation call still compiles
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B3E)
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { if (!isSaving) navController.popBackStack() },
                    enabled = !isSaving
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    "Add Driver",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            HorizontalDivider(color = Color.Gray)

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
                enabled = !isSaving,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Address") },
                enabled = !isSaving,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                enabled = !isSaving,
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Button(
                onClick = {
                    if (isSaving) return@Button

                    if (name.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Name cannot be empty") }
                        return@Button
                    }

                    if (phone.length < 7) {
                        scope.launch { snackbarHostState.showSnackbar("Phone number must be valid") }
                        return@Button
                    }

                    isSaving = true

                    scope.launch {
                        try {
                            // vehicleId is null: the driver starts unassigned
                            driverVm.add(
                                DriverEntity(
                                    name = name.trim(),
                                    address = address.trim(),
                                    phone = phone.trim()
                                )
                            )
                            navController.popBackStack()
                        } catch (e: Exception) {
                            isSaving = false
                            snackbarHostState.showSnackbar(
                                "Could not add driver: ${e.localizedMessage ?: "Please try again."}"
                            )
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Add Driver",
                    color = Color.White
                )
            }
        }
    }
}