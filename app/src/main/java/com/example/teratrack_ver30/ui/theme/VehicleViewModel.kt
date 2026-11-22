package com.example.teratrack_ver30.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teratrack_ver30.data.VehicleEntity
import com.example.teratrack_ver30.data.VehicleRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehicleViewModel(private val repo: VehicleRepository) : ViewModel() {

    val vehicles = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unassigned = repo.getUnassigned()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(v: VehicleEntity) = viewModelScope.launch { repo.add(v) }
    fun update(v: VehicleEntity) = viewModelScope.launch { repo.update(v) }
    fun delete(v: VehicleEntity) = viewModelScope.launch { repo.delete(v) }
}