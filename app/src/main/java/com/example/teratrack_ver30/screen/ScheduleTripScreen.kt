
package com.example.teratrack_ver30.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.teratrack_ver30.viewModel.TripViewModel
import com.example.teratrack_ver30.viewModel.VehicleViewModel
import com.example.teratrack_ver30.data.TripEntity
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTripScreen(
    navController: NavController,
    tripVm: TripViewModel,
    vehicleVm: VehicleViewModel,
    driverVm: DriverViewModel
) {
    val vehicles by vehicleVm.vehicles.collectAsState()
    val warehouses by tripVm.warehouses.collectAsState()
    val drivers by driverVm.drivers.collectAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isSaving by remember { mutableStateOf(false) }
    var selectedVehicleId by remember { mutableStateOf<Int?>(null) }
    var startPoint by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B3E)
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .padding(top = 16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        if (!isSaving) {
                            // ADDED: Return directly to Dashboard
                            navController.navigate("dashboard") {
                                popUpTo("dashboard") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    },
                    enabled = !isSaving
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back to Dashboard",
                        tint = Color.White
                    )
                }

                Text(
                    "Schedule Trip",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.Gray
            )

            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )

            // Vehicle Dropdown
            var vehicleExpanded by remember { mutableStateOf(false) }

            Box(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vehicles.firstOrNull {
                        it.id == selectedVehicleId
                    }?.vehicleId ?: "Select Vehicle",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle ID") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = fieldColors,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    Modifier
                        .matchParentSize()
                        .clickable(enabled = !isSaving) {
                            vehicleExpanded = true
                        }
                )

                DropdownMenu(
                    expanded = vehicleExpanded,
                    onDismissRequest = { vehicleExpanded = false }
                ) {
                    vehicles.forEach { vehicle ->
                        DropdownMenuItem(
                            text = { Text(vehicle.vehicleId) },
                            onClick = {
                                selectedVehicleId = vehicle.id
                                vehicleExpanded = false
                            }
                        )
                    }
                }
            }

            // Start Point Dropdown
            var startExpanded by remember { mutableStateOf(false) }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = if (startPoint.isEmpty()) "Select Start Point" else startPoint,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Start Point") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = fieldColors,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    Modifier
                        .matchParentSize()
                        .clickable(enabled = !isSaving) {
                            startExpanded = true
                        }
                )

                DropdownMenu(
                    expanded = startExpanded,
                    onDismissRequest = { startExpanded = false }
                ) {
                    warehouses.forEach { warehouse ->
                        DropdownMenuItem(
                            text = { Text(warehouse.warehouseName) },
                            onClick = {
                                startPoint = warehouse.warehouseName
                                startExpanded = false
                            }
                        )
                    }
                }
            }

            // Destination Dropdown
            var destinationExpanded by remember { mutableStateOf(false) }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = if (destination.isEmpty()) "Select Destination" else destination,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Destination") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = fieldColors,
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    Modifier
                        .matchParentSize()
                        .clickable(enabled = !isSaving) {
                            destinationExpanded = true
                        }
                )

                DropdownMenu(
                    expanded = destinationExpanded,
                    onDismissRequest = { destinationExpanded = false }
                ) {
                    warehouses.forEach { warehouse ->
                        DropdownMenuItem(
                            text = { Text(warehouse.warehouseName) },
                            onClick = {
                                destination = warehouse.warehouseName
                                destinationExpanded = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                enabled = !isSaving,
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            // Date Row
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Month
                var monthExpanded by remember { mutableStateOf(false) }

                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (month.isEmpty()) "Month" else month,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isSaving,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = fieldColors
                    )

                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable(enabled = !isSaving) {
                                monthExpanded = true
                            }
                    )

                    DropdownMenu(
                        expanded = monthExpanded,
                        onDismissRequest = { monthExpanded = false }
                    ) {
                        (1..12).forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value.toString()) },
                                onClick = {
                                    month = value.toString()
                                    monthExpanded = false
                                }
                            )
                        }
                    }
                }

                // Day
                var dayExpanded by remember { mutableStateOf(false) }

                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (day.isEmpty()) "Day" else day,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isSaving,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = fieldColors
                    )

                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable(enabled = !isSaving) {
                                dayExpanded = true
                            }
                    )

                    DropdownMenu(
                        expanded = dayExpanded,
                        onDismissRequest = { dayExpanded = false }
                    ) {
                        (1..31).forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value.toString()) },
                                onClick = {
                                    day = value.toString()
                                    dayExpanded = false
                                }
                            )
                        }
                    }
                }

                // Year
                var yearExpanded by remember { mutableStateOf(false) }

                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (year.isEmpty()) "Year" else year,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isSaving,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = fieldColors
                    )

                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable(enabled = !isSaving) {
                                yearExpanded = true
                            }
                    )

                    DropdownMenu(
                        expanded = yearExpanded,
                        onDismissRequest = { yearExpanded = false }
                    ) {
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                        (currentYear..2031).forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value.toString()) },
                                onClick = {
                                    year = value.toString()
                                    yearExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // ADD BUTTON
            Button(
                onClick = {
                    if (isSaving) return@Button

                    if (
                        selectedVehicleId == null ||
                        startPoint.isEmpty() ||
                        destination.isEmpty() ||
                        month.isEmpty() ||
                        day.isEmpty() ||
                        year.isEmpty()
                    ) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please fill all fields")
                        }
                        return@Button
                    }

                    isSaving = true
                    val dateStr = "$month/$day/$year"

                    val assignedDriver = drivers.firstOrNull {
                        it.vehicleId == selectedVehicleId
                    }

                    val newTrip = TripEntity(
                        vehicleId = selectedVehicleId!!,
                        driverName = assignedDriver?.name ?: "Unassigned",
                        description = description,
                        startPoint = startPoint,
                        destination = destination,
                        date = dateStr
                    )

                    scope.launch {
                        try {
                            tripVm.add(newTrip)

                            // Keep the existing save-success navigation
                            navController.popBackStack()
                        } catch (e: Exception) {
                            isSaving = false
                            snackbarHostState.showSnackbar(
                                "Could not schedule trip: ${
                                    e.localizedMessage ?: "Please try again."
                                }"
                            )
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800)
                )
            ) {
                Text(
                    if (isSaving) "Saving..." else "Add",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
