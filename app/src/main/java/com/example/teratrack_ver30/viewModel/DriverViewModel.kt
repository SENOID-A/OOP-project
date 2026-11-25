package com.example.teratrack_ver30.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teratrack_ver30.data.DriverEntity
import com.example.teratrack_ver30.repositories.DriverRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DriverViewModel(private val repo: DriverRepository) : ViewModel() {

    val drivers = repo.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun add(d: DriverEntity) {
        repo.add(d)
    }

    fun update(d: DriverEntity) = viewModelScope.launch {
        repo.update(d)
    }

    fun delete(d: DriverEntity) = viewModelScope.launch {
        repo.delete(d)
    }
}