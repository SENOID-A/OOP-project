
package com.example.teratrack_ver30.screen

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.teratrack_ver30.viewModel.VehicleViewModel
import com.example.teratrack_ver30.data.VehicleEntity
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicleScreen(navController: NavController, vm: VehicleViewModel) {

    var vehicleId by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isSaving by remember { mutableStateOf(false) }

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult

        try {
            val file = File(
                context.cacheDir,
                "vehicle_${System.currentTimeMillis()}.jpg"
            )

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            imagePath = file.absolutePath
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Failed to save image")
            }
        }
    }

    // Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap ?: return@rememberLauncherForActivityResult

        try {
            val file = File(
                context.cacheDir,
                "vehicle_${System.currentTimeMillis()}.jpg"
            )

            file.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, output)
            }

            imagePath = file.absolutePath
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Failed to save image")
            }
        }
    }

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
                    "Add Vehicle",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            HorizontalDivider(color = Color.Gray)

            // Vehicle ID
            OutlinedTextField(
                value = vehicleId,
                onValueChange = { vehicleId = it },
                label = { Text("Vehicle ID (e.g. V1-0)") },
                enabled = !isSaving,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            // Image Buttons
            Row(Modifier.padding(top = 16.dp)) {
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    )
                ) {
                    Text("Select Image")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = { cameraLauncher.launch(null) },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800),
                        contentColor = Color.White
                    )
                ) {
                    Text("Take Photo")
                }
            }

            // Image Preview
            imagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Vehicle Preview",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(top = 16.dp)
                    )
                }
            }

            // ADD BUTTON
            Button(
                onClick = {
                    if (isSaving) return@Button

                    if (vehicleId.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Vehicle ID cannot be empty")
                        }
                        return@Button
                    }

                    isSaving = true

                    scope.launch {
                        try {
                            vm.add(
                                VehicleEntity(
                                    vehicleId = vehicleId.trim(),
                                    imagePath = imagePath
                                )
                            )

                            // Keep the existing save-success navigation
                            navController.popBackStack()
                        } catch (e: Exception) {
                            isSaving = false
                            snackbarHostState.showSnackbar(
                                "Could not add vehicle: ${
                                    e.localizedMessage ?: "Please try again."
                                }"
                            )
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                )
            ) {
                Text(if (isSaving) "Saving..." else "Add Vehicle")
            }
        }
    }
}
