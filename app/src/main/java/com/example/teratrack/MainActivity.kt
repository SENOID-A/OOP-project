package com.example.teratrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.teratrack.ui.CalabarzonMapView
import com.example.teratrack.ui.MaintenanceAdapter
import com.example.teratrack.ui.TripAdapter
import com.example.teratrack.ui.VehicleAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: FleetViewModel
    private lateinit var contentFrame: FrameLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[FleetViewModel::class.java]
        contentFrame = findViewById(R.id.contentFrame)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            showPage(item.itemId)
            true
        }
        bottomNavigation.selectedItemId = R.id.nav_overview
    }

    private fun showPage(itemId: Int) {
        val layoutId = when (itemId) {
            R.id.nav_fleet -> R.layout.page_fleet
            R.id.nav_trips -> R.layout.page_trips
            R.id.nav_map -> R.layout.page_map
            R.id.nav_service -> R.layout.page_service
            else -> R.layout.page_overview
        }
        contentFrame.removeAllViews()
        val page = LayoutInflater.from(this).inflate(layoutId, contentFrame, false)
        contentFrame.addView(page)

        when (itemId) {
            R.id.nav_fleet -> bindFleet(page)
            R.id.nav_trips -> bindTrips(page)
            R.id.nav_map -> bindMap(page)
            R.id.nav_service -> bindMaintenance(page)
            else -> bindOverview(page)
        }
    }

    private fun bindOverview(page: View) {
        val vehicles = viewModel.vehicles()
        val trips = viewModel.trips()
        val records = viewModel.maintenanceRecords()
        val activeTrips = trips.count { it.isActive }
        val availableVehicles = vehicles.count { it.isAvailable }
        val openMaintenance = records.count {
            it.status.name == "SCHEDULED" || it.status.name == "IN_PROGRESS"
        }

        metric(page, R.id.metricVehicles, vehicles.size.toString(), "Registered vehicles")
        metric(page, R.id.metricTrips, activeTrips.toString(), "Active trips")
        metric(page, R.id.metricAvailable, availableVehicles.toString(), "Available vehicles")
        metric(page, R.id.metricService, openMaintenance.toString(), "Open maintenance")

        val recycler = page.findViewById<RecyclerView>(R.id.overviewRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = VehicleAdapter(vehicles.take(3))
        page.findViewById<View>(R.id.overviewEmpty).visibility =
            if (vehicles.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun metric(page: View, id: Int, value: String, label: String) {
        val card = page.findViewById<View>(id)
        card.findViewById<TextView>(R.id.metricValue).text = value
        card.findViewById<TextView>(R.id.metricLabel).text = label
    }

    private fun bindFleet(page: View) {
        val vehicles = viewModel.vehicles()
        val recycler = page.findViewById<RecyclerView>(R.id.listRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = VehicleAdapter(vehicles)
        page.findViewById<TextView>(R.id.emptyText).visibility =
            if (vehicles.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun bindTrips(page: View) {
        val trips = viewModel.trips()
        val recycler = page.findViewById<RecyclerView>(R.id.listRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TripAdapter(trips, viewModel.vehicles()) { trip ->
            try {
                viewModel.advanceTrip(trip.id)
                showPage(R.id.nav_trips)
            } catch (error: Exception) {
                Toast.makeText(this, error.message ?: "Unable to update trip", Toast.LENGTH_LONG).show()
            }
        }
        page.findViewById<TextView>(R.id.emptyText).visibility =
            if (trips.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun bindMaintenance(page: View) {
        val records = viewModel.maintenanceRecords()
        val recycler = page.findViewById<RecyclerView>(R.id.listRecycler)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = MaintenanceAdapter(records, viewModel.vehicles())
        page.findViewById<TextView>(R.id.emptyText).visibility =
            if (records.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun bindMap(page: View) {
        val warehouses = viewModel.warehouses()
        val vehicles = viewModel.vehicles()
        page.findViewById<CalabarzonMapView>(R.id.calabarzonMap).setData(warehouses, vehicles)
        page.findViewById<TextView>(R.id.mapSummary).text =
            "${warehouses.size} distributor points  ·  ${vehicles.size} registered vehicles"
    }
}
