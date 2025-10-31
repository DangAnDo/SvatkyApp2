package com.example.svatkyapp.ui.dashboard

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DashboardViewModel : ViewModel() {

    // vstupy
    var inputDate: String = ""

    // výstupy
    private val _resultDate = MutableStateFlow<String?>(null)
    val resultDate: StateFlow<String?> get() = _resultDate
    private val _resultName = MutableStateFlow<String?>(null)

    fun searchByDate() {
        if (inputDate.isBlank()) {
            _resultDate.value = "Zadejte prosím datum."
            return
        }

        // TODO: API
        _resultDate.value = "funguje"
    }
}
