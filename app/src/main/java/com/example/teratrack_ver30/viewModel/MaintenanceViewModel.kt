package com.example.teratrack_ver30.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teratrack_ver30.data.MaintenanceEntity
import com.example.teratrack_ver30.repositories.MaintenanceRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MaintenanceViewModel(private val repo: MaintenanceRepository) : ViewModel() {

    val maintenanceRecords = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun add(m: MaintenanceEntity) {
        repo.add(m)
    }

    fun update(m: MaintenanceEntity) = viewModelScope.launch {
        repo.update(m)
    }

    fun delete(m: MaintenanceEntity) = viewModelScope.launch {
        repo.delete(m)
    }
}