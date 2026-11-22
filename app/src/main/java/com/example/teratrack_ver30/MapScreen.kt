
package com.example.teratrack_ver30

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.net.HttpURLConnection
import java.net.URL

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

    val activeTrips = trips.filter { it.status != "Completed" }

    // Configure OSMDroid before creating the map.
    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences(
                "osmdroid",
                Context.MODE_PRIVATE
            ))

            userAgentValue = context.packageName

            additionalHttpRequestProperties["Referer"] =
                "https://github.com/SENOID-A/OOP-project/"
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
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

    LaunchedEffect(activeTrips, warehouses) {
        mapView.overlays.clear()

        var pointsForZoom = mutableListOf<GeoPoint>()

        for (trip in activeTrips) {

            val startWarehouse = warehouses.firstOrNull {
                it.warehouseName == trip.startPoint
            }

            val destWarehouse = warehouses.firstOrNull {
                it.warehouseName == trip.destination
            }

            if (startWarehouse == null || destWarehouse == null) {
                continue
            }

            val startGeo = GeoPoint(
                startWarehouse.latitude,
                startWarehouse.longitude
            )

            val destGeo = GeoPoint(
                destWarehouse.latitude,
                destWarehouse.longitude
            )

            // Add starting marker.
            val startMarker = Marker(mapView).apply {
                position = startGeo
                title = "Starting Point: ${trip.startPoint}"
                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )
            }

            // Add destination marker.
            val destMarker = Marker(mapView).apply {
                position = destGeo
                title = "Destination: ${trip.destination}"
                setAnchor(
                    Marker.ANCHOR_CENTER,
                    Marker.ANCHOR_BOTTOM
                )
            }

            mapView.overlays.add(startMarker)
            mapView.overlays.add(destMarker)

            // Get the actual road route from OSRM.
            val routePoints = fetchRoadRoute(startGeo, destGeo)

            if (routePoints.size >= 2) {
                val roadRoute = Polyline().apply {
                    setPoints(routePoints)
                    title = "Trip Route"
                }

                mapView.overlays.add(roadRoute)
                pointsForZoom.addAll(routePoints)
            } else {
                pointsForZoom.add(startGeo)
                pointsForZoom.add(destGeo)
            }
        }

        if (pointsForZoom.isNotEmpty()) {
            mapView.zoomToBoundingBox(
                org.osmdroid.util.BoundingBox.fromGeoPoints(
                    pointsForZoom
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

        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

// Retrieves the actual road route using OSRM.
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

            val responseCode = connection.responseCode

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext emptyList()
            }

            val response = connection.inputStream
                .bufferedReader()
                .use { it.readText() }

            val json = JSONObject(response)

            val routes = json.getJSONArray("routes")

            if (routes.length() == 0) {
                return@withContext emptyList()
            }

            val geometry = routes
                .getJSONObject(0)
                .getJSONObject("geometry")

            val coordinates = geometry.getJSONArray("coordinates")

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
