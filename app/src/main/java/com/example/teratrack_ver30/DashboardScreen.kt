
package com.example.teratrack_ver30

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    userName: String,
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
    val context = LocalContext.current

    // Get the saved profile image for this account.
    val preferences = remember {
        context.getSharedPreferences("TeraTrackProfile", android.content.Context.MODE_PRIVATE)
    }

    var profileImageUri by remember(userName) {
        mutableStateOf(
            preferences.getString("profile_$userName", null)
        )
    }

    // Open the phone's image picker.
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Continue even if persistent permission is unavailable.
            }

            profileImageUri = uri.toString()

            // Save the selected image for this account.
            preferences.edit()
                .putString("profile_$userName", uri.toString())
                .apply()
        }
    }

    // Load the saved image.
    val profileBitmap: Bitmap? = remember(profileImageUri) {
        if (profileImageUri != null) {
            try {
                context.contentResolver
                    .openInputStream(Uri.parse(profileImageUri!!))
                    ?.use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = darkBlue,
                modifier = Modifier.width(280.dp)
            ) {

                // USER PROFILE SECTION
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Clickable profile picture.
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                            .clickable {
                                imagePicker.launch(arrayOf("image/*"))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap.asImageBitmap(),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Select Profile Picture",
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // Actual username from the database.
                    Text(
                        text = userName.ifBlank { "User" },
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider(
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = {
                        Text("Fleet Map", color = Color.White)
                    },
                    icon = {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    selected = false,
                    onClick = {
                        scope.launch {
                            drawerState.close()
                        }
                        onNavigateToMap()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color.Transparent
                    )
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

            // TOP HEADER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(darkBlue)
                    .padding(
                        top = 40.dp,
                        bottom = 60.dp,
                        start = 20.dp,
                        end = 20.dp
                    )
            ) {

                // Menu button stays on the upper-left.
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Center the TeraTrack title.
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TeraTrack",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // FLOATING LOGO BADGE
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 140.dp)
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(darkBlue),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(
                        id = R.drawable.icon
                    ),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
            }

            // SCROLLABLE DASHBOARD GRID
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = 300.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                    .verticalScroll(rememberScrollState())
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
                painter = androidx.compose.ui.res.painterResource(id = iconResId),
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue
                    ),
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "view",
                        fontSize = 20.sp,
                        color = Color.White
                    )
                }

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
