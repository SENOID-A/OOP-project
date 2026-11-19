package com.example.teratrack.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.teratrack.R
import maintenance.MaintenanceRecord
import vehicle.Vehicle
import java.text.SimpleDateFormat
import java.util.Locale

class MaintenanceAdapter(private val items: List<MaintenanceRecord>, private val vehicles: List<Vehicle>) : RecyclerView.Adapter<MaintenanceAdapter.Holder>() {
    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val vehicle: TextView = v.findViewById(R.id.maintenanceVehicle)
        val description: TextView = v.findViewById(R.id.maintenanceDescription)
        val status: TextView = v.findViewById(R.id.maintenanceStatus)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance, parent, false))
    override fun getItemCount() = items.size
    override fun onBindViewHolder(h: Holder, position: Int) {
        val r = items[position]
        h.vehicle.text = vehicles.firstOrNull { it.id == r.vehicleId }?.plateNumber ?: r.vehicleId
        val dateStr = r.scheduledDate.toString().substringBefore('T')
        val parsed = try { SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(dateStr) } catch (_: Exception) { null }
        val date = if (parsed != null) SimpleDateFormat("dd MMM yyyy", Locale.US).format(parsed) else dateStr
        h.description.text = "$date  ·  ${r.description}"
        h.status.text = r.status.name.replace('_',' ')
    }
}
