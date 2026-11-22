package com.example.teratrack_ver30.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.teratrack_ver30.viewModel.TripViewModel
import com.example.teratrack_ver30.viewModel.VehicleViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.HttpURLConnection
import java.net.URL
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

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
    val vehicles by vehicleVm.vehicles.collectAsState()

    var selectedTripId by remember {
        mutableStateOf<Int?>(null)
    }

    var expanded by remember {
        mutableStateOf(false)
    }

    var routeMessage by remember {
        mutableStateOf("Select a trip to view its route.")
    }

    val selectedTrip = trips.firstOrNull {
        it.id == selectedTripId
    } ?: trips.firstOrNull()

    val selectedVehicle = selectedTrip?.let { trip ->
        vehicles.firstOrNull {
            it.id == trip.vehicleId
        }
    }

    // MAP INITIALIZATION
    val mapTilerTiles = object : OnlineTileSourceBase(
        "MapTiler", 0, 20, 256, ".png",
        arrayOf("https://api.maptiler.com/maps/streets-v2/256/")
    ) {
        override fun getTileURLString(pMapTileIndex: Long): String =
            baseUrl +
                    MapTileIndex.getZoom(pMapTileIndex) + "/" +
                    MapTileIndex.getX(pMapTileIndex) + "/" +
                    MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding +
                    "?key=zdBrk8JVCeq9qMNKGvLb"
    }

    val mapView = remember(context) {
        MapView(context).apply {
            setTileSource(mapTilerTiles)
            setMultiTouchControls(true)

            controller.setZoom(12.0)
            controller.setCenter(
                GeoPoint(14.2091219, 121.1554600)
            )
        }
    }

    DisposableEffect(mapView) {
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    // LOAD AND DRAW THE SELECTED TRIP ROUTE
    LaunchedEffect(selectedTrip, warehouses) {

        mapView.overlays.clear()

        val trip = selectedTrip

        if (trip == null) {
            routeMessage = "No scheduled trips available."
            mapView.invalidate()
            return@LaunchedEffect
        }

        routeMessage = "Finding warehouse locations..."

        val startWarehouse = warehouses.firstOrNull {
            it.warehouseName.trim().equals(
                trip.startPoint.trim(),
                ignoreCase = true
            )
        }

        val destinationWarehouse = warehouses.firstOrNull {
            it.warehouseName.trim().equals(
                trip.destination.trim(),
                ignoreCase = true
            )
        }

        if (startWarehouse == null || destinationWarehouse == null) {
            routeMessage =
                "Warehouse coordinates not found. Check your warehouse records."

            mapView.invalidate()
            return@LaunchedEffect
        }

        val startPoint = GeoPoint(
            startWarehouse.latitude,
            startWarehouse.longitude
        )

        val destinationPoint = GeoPoint(
            destinationWarehouse.latitude,
            destinationWarehouse.longitude
        )

        // VEHICLE MARKER
        val vehicleMarker = Marker(mapView).apply {
            position = startPoint
            title = "Vehicle: ${selectedVehicle?.vehicleId ?: "Unknown"}"
            snippet = "Driver: ${trip.driverName}"

            icon = createVehicleIcon(context)

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
        }

        // DESTINATION MARKER
        val destinationMarker = Marker(mapView).apply {
            position = destinationPoint
            title = "Destination: ${trip.destination}"
            snippet = "Trip #${trip.id}"

            setAnchor(
                Marker.ANCHOR_CENTER,
                Marker.ANCHOR_BOTTOM
            )
        }

        mapView.overlays.add(vehicleMarker)
        mapView.overlays.add(destinationMarker)

        // FETCH ROAD ROUTE
        routeMessage = "Loading road route..."

        val routePoints = fetchRoadRoute(
            startPoint,
            destinationPoint
        )

        if (routePoints.size >= 2) {

            val routeLine = Polyline().apply {
                setPoints(routePoints)
                title = "Trip #${trip.id} Route"
                setColor(android.graphics.Color.BLUE)
                setWidth(12f)
            }

            mapView.overlays.add(routeLine)

            routeMessage = "Road route loaded successfully."

            val allPoints = routePoints + startPoint + destinationPoint

            mapView.zoomToBoundingBox(
                BoundingBox.fromGeoPoints(allPoints),
                true,
                100
            )

        } else {

            routeMessage =
                "Unable to retrieve the road route. Check your internet connection or routing service."

            mapView.zoomToBoundingBox(
                BoundingBox.fromGeoPoints(
                    listOf(startPoint, destinationPoint)
                ),
                true,
                100
            )
        }

        mapView.invalidate()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Fleet Map")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
                .clipToBounds()
        ) {

            // =====================================
            // TRIP INFORMATION — TOP 45%
            // =====================================

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.45f, fill = true)
                    .verticalScroll(rememberScrollState())
                    .clipToBounds()
            ) {

                // TRIP SELECTOR
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(
                        value = selectedTrip?.let {
                            "Trip #${it.id}: ${it.startPoint} → ${it.destination}"
                        } ?: "No trips available",
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Select Trip")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = trips.isNotEmpty()
                    )

                    if (trips.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(65.dp)
                                .align(Alignment.Center)
                                .clickable {
                                    expanded = true
                                }
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        trips.forEach { trip ->

                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            "Trip #${trip.id}",
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            "${trip.startPoint} → ${trip.destination}",
                                            fontSize = 12.sp
                                        )

                                        Text(
                                            "${trip.date} | ${trip.status}",
                                            fontSize = 11.sp
                                        )
                                    }
                                },
                                onClick = {
                                    selectedTripId = trip.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // TRIP DETAILS
                selectedTrip?.let { trip ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {

                            Text(
                                "Trip Information",
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                "Vehicle: ${selectedVehicle?.vehicleId ?: "Unknown"}"
                            )

                            Text(
                                "Driver: ${trip.driverName}"
                            )

                            Text(
                                "Description: ${trip.description.ifBlank { "None" }}"
                            )

                            Text(
                                "Starting Point: ${trip.startPoint}"
                            )

                            Text(
                                "Destination: ${trip.destination}"
                            )

                            Text(
                                "Scheduled Date: ${trip.date}"
                            )

                            Text(
                                "Status: ${trip.status}"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ROUTE STATUS
                Text(
                    text = routeMessage,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // =====================================
            // MAP — BOTTOM 55%
            // =====================================

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.55f, fill = true)
                    .clipToBounds()
            ) {

                AndroidView(
                    factory = {
                        mapView
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = {
                        it.onResume()
                    }
                )

                Text(
                    text = "© OpenStreetMap contributors",
                    fontSize = 10.sp,
                    color = Color.DarkGray,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            Color.White.copy(alpha = 0.85f)
                        )
                        .padding(4.dp)
                )
            }
        }
    }
}

// =====================================
// VEHICLE ICON
// =====================================

fun createVehicleIcon(context: Context): BitmapDrawable {

    val size = 128

    val bitmap = Bitmap.createBitmap(
        size,
        size,
        Bitmap.Config.ARGB_8888
    )

    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 96f
        textAlign = Paint.Align.CENTER
    }

    canvas.drawText(
        "🚚",
        size / 2f,
        100f,
        paint
    )

    return BitmapDrawable(
        context.resources,
        bitmap
    )
}

// =====================================
// OSRM ROAD ROUTE FETCHING
// =====================================

suspend fun fetchRoadRoute(
    start: GeoPoint,
    end: GeoPoint
): List<GeoPoint> {

    return withContext(Dispatchers.IO) {

        var connection: HttpURLConnection? = null

        try {

            val urlString =
                "https://router.project-osrm.org/route/v1/driving/" +
                        "${start.longitude},${start.latitude};" +
                        "${end.longitude},${end.latitude}" +
                        "?overview=full&geometries=geojson"

            val url = URL(urlString)

            connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000

            connection.setRequestProperty(
                "User-Agent",
                "TeraTrack/1.0 (com.example.teratrack_ver30)"
            )

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext emptyList()
            }

            val response = connection.inputStream
                .bufferedReader()
                .use {
                    it.readText()
                }

            val json = JSONObject(response)

            if (json.optString("code") != "Ok") {
                return@withContext emptyList()
            }

            val routes = json.getJSONArray("routes")

            if (routes.length() == 0) {
                return@withContext emptyList()
            }

            val coordinates = routes
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            val routePoints = mutableListOf<GeoPoint>()

            for (i in 0 until coordinates.length()) {

                val coordinate = coordinates.getJSONArray(i)

                val longitude = coordinate.getDouble(0)
                val latitude = coordinate.getDouble(1)

                routePoints.add(
                    GeoPoint(latitude, longitude)
                )
            }

            routePoints

        } catch (e: Exception) {

            e.printStackTrace()
            emptyList()

        } finally {
            connection?.disconnect()
        }
    }
}
