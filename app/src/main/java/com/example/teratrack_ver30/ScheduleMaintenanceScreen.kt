package com.example.teratrack_ver30

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
import com.example.teratrack_ver30.data.MaintenanceEntity
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleMaintenanceScreen(
    navController: NavController,
    maintenanceVm: MaintenanceViewModel,
    vehicleVm: VehicleViewModel
) {
    val vehicles by vehicleVm.vehicles.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedVehicleId by remember { mutableStateOf<Int?>(null) }
    var problem by remember { mutableStateOf("") }
    var month by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B3E) // <-- CHANGED TO NAVY BLUE
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp).padding(top = 16.dp)) {

            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Schedule Maintenance", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp), color = Color.Gray)

            // Shared Field Colors (White Background, Black Text)
            val fieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )

            // 1. Vehicle Dropdown
            var vehicleExpanded by remember { mutableStateOf(false) }
            Box(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vehicles.firstOrNull { it.id == selectedVehicleId }?.vehicleId ?: "Select Vehicle ID",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Vehicle ID") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(Modifier.matchParentSize().clickable { vehicleExpanded = true })
                DropdownMenu(expanded = vehicleExpanded, onDismissRequest = { vehicleExpanded = false }) {
                    vehicles.forEach { v ->
                        DropdownMenuItem(
                            text = { Text(v.vehicleId) },
                            onClick = { selectedVehicleId = v.id; vehicleExpanded = false }
                        )
                    }
                }
            }

            // 2. Date Row
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // Month
                var mExp by remember { mutableStateOf(false) }
                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (month.isEmpty()) "Month" else month,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = fieldColors
                    )
                    Box(Modifier.matchParentSize().clickable { mExp = true })
                    DropdownMenu(expanded = mExp, onDismissRequest = { mExp = false }) {
                        (1..12).forEach { DropdownMenuItem(text = { Text(it.toString()) }, onClick = { month = it.toString(); mExp = false }) }
                    }
                }

                // Day
                var dExp by remember { mutableStateOf(false) }
                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (day.isEmpty()) "Day" else day,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = fieldColors
                    )
                    Box(Modifier.matchParentSize().clickable { dExp = true })
                    DropdownMenu(expanded = dExp, onDismissRequest = { dExp = false }) {
                        (1..31).forEach { DropdownMenuItem(text = { Text(it.toString()) }, onClick = { day = it.toString(); dExp = false }) }
                    }
                }

                // Year
                var yExp by remember { mutableStateOf(false) }
                Box(Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = if (year.isEmpty()) "Year" else year,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                        colors = fieldColors
                    )
                    Box(Modifier.matchParentSize().clickable { yExp = true })
                    DropdownMenu(expanded = yExp, onDismissRequest = { yExp = false }) {
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                        (currentYear..2031).forEach { DropdownMenuItem(text = { Text(it.toString()) }, onClick = { year = it.toString(); yExp = false }) }
                    }
                }
            }

            // 3. Problem Text Field
            OutlinedTextField(
                value = problem,
                onValueChange = { problem = it },
                label = { Text("Problem to Fix") },
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            // 4. Add Button
            Button(
                onClick = {
                    if (selectedVehicleId == null || problem.isBlank() || month.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar("Please fill all fields") }
                        return@Button
                    }
                    val dateStr = "$month/$day/$year"
                    maintenanceVm.add(MaintenanceEntity(vehicleId = selectedVehicleId!!, problem = problem, date = dateStr))
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("Add", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}