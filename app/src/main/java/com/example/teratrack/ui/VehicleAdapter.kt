package com.example.teratrack.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teratrack.R
import vehicle.Vehicle
import java.util.Locale

class VehicleAdapter(private val items: List<Vehicle>) : RecyclerView.Adapter<VehicleAdapter.Holder>() {
    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val plate: TextView = v.findViewById(R.id.vehiclePlate)
        val meta: TextView = v.findViewById(R.id.vehicleMeta)
        val location: TextView = v.findViewById(R.id.vehicleLocation)
        val status: TextView = v.findViewById(R.id.vehicleStatus)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: Holder, position: Int) {
        val v = items[position]
        h.plate.text = v.plateNumber
        h.meta.text = "${v.type.name.replace('_',' ')}  ·  ${v.capacityKg.toInt()} kg  ·  Fuel ${v.fuelLevelPct.toInt()}%"
        h.location.text = String.format(Locale.US, "%.4f, %.4f", v.currentLocation.latitude, v.currentLocation.longitude)
        h.status.text = v.status.name.replace('_',' ')
    }
}
