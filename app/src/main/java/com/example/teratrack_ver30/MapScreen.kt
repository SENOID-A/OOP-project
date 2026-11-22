package com.example.teratrack_ver30

import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    tripVm: TripViewModel,
    vehicleVm: VehicleViewModel
) {
    val context = LocalContext.current
    val trips by tripVm.trips.collectAsState()
    val warehouses by tripVm.warehouses.collectAsState()

    // Force User-Agent to prevent OSM Access Blocked errors
    Configuration.getInstance().userAgentValue = "TeraTrack_StudentApp/1.0 (com.example.teratrack_ver30)"

    // Only show trips that are NOT completed
    val activeTrips = trips.filter { it.status != "Completed" }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK) // Standard, reliable map
            setMultiTouchControls(true)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            controller.setZoom(9.0)
            controller.setCenter(GeoPoint(14.2091219, 121.1554600))
        }
    }

    // Update markers and lines whenever trips or warehouses change
    LaunchedEffect(activeTrips, warehouses) {
        mapView.overlays.clear()
        activeTrips.forEach { trip ->
            val startWarehouse = warehouses.firstOrNull { it.warehouseName == trip.startPoint }
            val destWarehouse = warehouses.firstOrNull { it.warehouseName == trip.destination }

            if (startWarehouse != null && destWarehouse != null) {
                val startGeo = GeoPoint(startWarehouse.latitude, startWarehouse.longitude)
                val destGeo = GeoPoint(destWarehouse.latitude, destWarehouse.longitude)

                // 1. Draw Line
                val line = Polyline(mapView).apply {
                    setPoints(listOf(startGeo, destGeo))
                    color = android.graphics.Color.RED
                    width = 5f
                }
                mapView.overlays.add(line)

                // 2. Add Marker
                val marker = Marker(mapView).apply {
                    position = destGeo
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Vehicle: ${trip.vehicleId}"
                    snippet = "Driver: ${trip.driverName}\nFrom: ${trip.startPoint}\nTo: ${trip.destination}\nStatus: ${trip.status}"
                }
                mapView.overlays.add(marker)
            }
        }
        mapView.invalidate()
    }

    // --- THIS WAS MISSING FROM YOUR FILE ---
    Scaffold(containerColor = Color(0xFF0D1B3E)) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text("Fleet Map", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Map
            AndroidView(
                factory = { mapView },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}