package com.example.teratrack.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teratrack.R
import com.google.android.material.button.MaterialButton
import trip.Trip
import trip.TripStatus
import vehicle.Vehicle
import java.util.Locale

class TripAdapter(private val items: List<Trip>, private val vehicles: List<Vehicle>, private val onAction: (Trip) -> Unit) : RecyclerView.Adapter<TripAdapter.Holder>() {
    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val vehicle: TextView = v.findViewById(R.id.tripVehicle)
        val status: TextView = v.findViewById(R.id.tripStatus)
        val route: TextView = v.findViewById(R.id.tripRoute)
        val details: TextView = v.findViewById(R.id.tripDetails)
        val action: MaterialButton = v.findViewById(R.id.tripAction)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: Holder, position: Int) {
        val t = items[position]
        h.vehicle.text = vehicles.firstOrNull { it.id == t.vehicleId }?.plateNumber ?: "Vehicle ${t.vehicleId.take(8)}"
        h.status.text = t.status.name.replace('_',' ')
        h.route.text = String.format(Locale.US, "%.4f, %.4f   →   %.4f, %.4f", t.origin.latitude, t.origin.longitude, t.destination.latitude, t.destination.longitude)
        h.details.text = "Driver ${t.driverId}  ·  ${t.cargoWeightKg.toInt()} kg cargo  ·  ${"%.1f".format(t.plannedDistanceKm)} km planned"
        val actionable = t.status == TripStatus.SCHEDULED || t.status == TripStatus.IN_PROGRESS
        h.action.visibility = if (actionable) View.VISIBLE else View.GONE
        h.action.text = if (t.status == TripStatus.SCHEDULED) "Start trip" else "Complete trip"
        h.action.setOnClickListener { onAction(t) }
    }
}
