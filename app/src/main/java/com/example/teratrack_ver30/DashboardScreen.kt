package com.example.teratrack_ver30

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onNavigateToVehicles: () -> Unit,
    onNavigateToDrivers: () -> Unit,
    onNavigateToAddVehicle: () -> Unit,
    onNavigateToAddDriver: () -> Unit,
    onNavigateToMaintenance: () -> Unit,
    onNavigateToScheduleMaintenance: () -> Unit,
    onNavigateToTrips: () -> Unit,
    onNavigateToScheduleTrip: () -> Unit,
    onNavigateToMap: () -> Unit
) {
    val darkBlue = Color(0xFF0D1B3E)
    val backgroundGray = Color(0xFFD9D9D9)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = darkBlue,
                modifier = Modifier.width(280.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(50)).background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("User Name", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider(color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text("Fleet Map", color = Color.White) },
                    icon = { Icon(Icons.Default.Map, contentDescription = null, tint = Color.White) },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToMap()
                    },
                    colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                )
            }
        }
    ) {
        // MAIN DASHBOARD CONTENT
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
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { scope.launch { drawerState.open() } }
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
                    .padding(top = 140.dp) // Adjusted
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(darkBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
            }

            // THE SCROLLABLE GRID
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 230.dp, start = 16.dp, end = 16.dp, bottom = 16.dp) // Adjusted top padding
                    .verticalScroll(rememberScrollState()) // <-- THIS FIXES THE MISSING BUTTONS
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardCard(
                        title = "Vehicle",
                        iconResId = R.drawable.truck,
                        modifier = Modifier.weight(1f),
                        onViewClick = onNavigateToVehicles,
                        onAddClick = onNavigateToAddVehicle,
                        darkBlue = darkBlue
                    )
                    DashboardCard(
                        title = "Driver",
                        iconResId = R.drawable.driver,
                        modifier = Modifier.weight(1f),
                        onViewClick = onNavigateToDrivers,
                        onAddClick = onNavigateToAddDriver,
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
                        iconResId = R.drawable.maintenance,
                        modifier = Modifier.weight(1f),
                        onViewClick = onNavigateToMaintenance,
                        onAddClick = onNavigateToScheduleMaintenance,
                        darkBlue = darkBlue
                    )
                    DashboardCard(
                        title = "Setup Trips",
                        iconResId = R.drawable.trip,
                        modifier = Modifier.weight(1f),
                        onViewClick = onNavigateToTrips,
                        onAddClick = onNavigateToScheduleTrip,
                        darkBlue = darkBlue
                    )
                }
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
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(64.dp),
                tint = Color.Black
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onViewClick,
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "view", fontSize = 20.sp, color = Color.White)
                }
                IconButton(
                    onClick = onAddClick,
                    modifier = Modifier.size(32.dp).background(Color.Transparent)
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