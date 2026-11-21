package com.example.teratrack_ver30

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DashboardScreen(
    onNavigateToVehicles: () -> Unit,
    onNavigateToDrivers: () -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToTrips: () -> Unit
) {
    // The dark navy blue from your screenshot
    val darkBlue = Color(0xFF0D1B3E)
    val backgroundGray = Color(0xFFD9D9D9)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGray)
    ) {
        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(darkBlue)
                .padding(top = 40.dp, bottom = 60.dp, start = 20.dp, end = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "TeraTrack",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Floating Logo Badge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp) // Adjust based on header height
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(darkBlue),
            contentAlignment = Alignment.Center
        ) {
            // Replace with your actual logo drawable
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "Logo",
//                tint = Color(0xFFFF6B00),
                modifier = Modifier.size(100.dp)
            )
        }

        // The 2x2 Grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 250.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    title = "Vehicle",
                    iconResId = R.drawable.truck, // Replace with your truck icon
                    modifier = Modifier.weight(1f),
                    onViewClick = onNavigateToVehicles,
                    onAddClick = { /* TODO: Navigate to Add Vehicle */ },
                    darkBlue = darkBlue
                )
                DashboardCard(
                    title = "Driver",
                    iconResId = R.drawable.driver, // Replace with your driver icon
                    modifier = Modifier.weight(1f),
                    onViewClick = onNavigateToDrivers,
                    onAddClick = { /* TODO: Navigate to Add Driver */ },
                    darkBlue = darkBlue
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DashboardCard(
                    title = "Maintenance",
                    iconResId = R.drawable.maintenance, // Replace with your clipboard icon
                    modifier = Modifier.weight(1f),
                    onViewClick = onNavigateToMaintenance,
                    onAddClick = { /* TODO: Navigate to Add Maintenance */ },
                    darkBlue = darkBlue
                )
                DashboardCard(
                    title = "Setup Trips",
                    iconResId = R.drawable.trip, // Replace with your route icon
                    modifier = Modifier.weight(1f),
                    onViewClick = onNavigateToTrips,
                    onAddClick = { /* TODO: Navigate to Add Trip */ },
                    darkBlue = darkBlue
                )
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    iconResId: Int,
    modifier: Modifier = Modifier,
    onViewClick: () -> Unit,
    onAddClick: () -> Unit,
    darkBlue: Color
) {
    Card(
        modifier = modifier.aspectRatio(0.85f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )

            // Icon
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                tint = Color.Black
            )

            // Bottom Buttons (View and +)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Button
                Button(
                    onClick = onViewClick,
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                    shape = RoundedCornerShape(50), // Pill shape
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "view", fontSize = 20.sp, color = Color.White)
                }

                // Plus Icon Button
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}