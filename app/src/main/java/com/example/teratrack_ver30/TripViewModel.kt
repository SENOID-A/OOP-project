package com.example.teratrack_ver30

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teratrack_ver30.data.TripEntity
import com.example.teratrack_ver30.data.TripRepository
import com.example.teratrack_ver30.data.WarehouseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TripViewModel(
    private val repo: TripRepository,
    private val warehouseRepo: WarehouseRepository
) : ViewModel() {

    val trips = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val warehouses = warehouseRepo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun add(t: TripEntity) {
        repo.add(t)
    }

    fun update(t: TripEntity) = viewModelScope.launch {
        repo.update(t)
    }

    fun delete(t: TripEntity) = viewModelScope.launch {
        repo.delete(t)
    }
}