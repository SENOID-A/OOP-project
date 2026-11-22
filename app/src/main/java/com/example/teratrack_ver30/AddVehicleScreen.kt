package com.example.teratrack_ver30

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
import com.example.teratrack_ver30.VehicleViewModel
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

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        try {
            val file = File(context.cacheDir, "vehicle_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output -> input.copyTo(output) }
            }
            imagePath = file.absolutePath
        } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar("Failed to save image") }
        }
    }

    // Camera
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap ?: return@rememberLauncherForActivityResult
        try {
            val file = File(context.cacheDir, "vehicle_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            imagePath = file.absolutePath
        } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar("Failed to save image") }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFF0D1B3E) // <-- CHANGED TO DARK NAVY BLUE
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Add Vehicle", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            HorizontalDivider(color = Color.Gray)

            // Text Field (White background, Black text)
            OutlinedTextField(
                value = vehicleId,
                onValueChange = { vehicleId = it },
                label = { Text("Vehicle ID (e.g. V1-0)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )

            // Image Buttons (Orange)
            Row(Modifier.padding(top = 16.dp)) {
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
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
                val bmp = BitmapFactory.decodeFile(path)
                if (bmp != null) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Preview",
                        modifier = Modifier.size(120.dp).padding(top = 16.dp)
                    )
                }
            }

            // Save Button (Orange)
            Button(
                onClick = {
                    if (vehicleId.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Vehicle ID cannot be empty") }
                        return@Button
                    }
                    vm.add(VehicleEntity(vehicleId = vehicleId, imagePath = imagePath))
                    scope.launch { snackbarHostState.showSnackbar("Vehicle added!") }
                    navController.popBackStack()
                },
                modifier = Modifier.padding(top = 24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White
                )
            ) {
                Text("Add Vehicle")
            }
        }
    }
}