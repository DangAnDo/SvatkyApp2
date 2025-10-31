package com.example.svatkyapp.ui.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import androidx.lifecycle.viewModelScope
import com.example.svatkyapp.data.NamedayApiService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers

class SearchViewModel : ViewModel() {

    var inputDate: String = ""
    var inputName: String = ""

    private val _resultDate = MutableStateFlow<String?>(null)
    val resultDate: StateFlow<String?> get() = _resultDate
    private val _resultName = MutableStateFlow<String?>(null)
    val resultName: StateFlow<String?> get() = _resultName
    private var namedays: Map<String, List<String>> = emptyMap()

    // JSONu ze složky assets
    fun loadNamedaysFromAssets(context: android.content.Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.assets.open("namedays.json")
                val reader = InputStreamReader(inputStream)
                @Suppress("UNCHECKED_CAST")
                val data = Gson().fromJson(reader, Map::class.java) as Map<String, List<String>>

                // Převod na lowercase
                namedays = data.mapKeys { it.key.lowercase() }

            } catch (e: Exception) {
                e.printStackTrace()
                namedays = emptyMap()
            }
        }
    }

    fun searchByDate() {
        if (inputDate.isBlank()) {
            _resultDate.value = "Zadejte prosím datum."
            return
        }
        val formatted = formatDateWithYear(inputDate)
        if (formatted == null) {
            _resultDate.value = "Nepodporovaný datum."
            return
        }
        viewModelScope.launch {
            val apiName = NamedayApiService.getNamedayForDate(formatted)
            _resultDate.value = apiName ?: "Jméno nenalezeno pro zadané datum."
        }
    }
    fun searchByName() {
        if (inputName.isBlank()) {
            _resultName.value = "Zadejte prosím jméno."
            return
        }
        val normalized = inputName.trim().lowercase()
        val dates = namedays[normalized]
        _resultName.value = dates?.joinToString(", ") ?: "Datum nenalezeno pro zadané jméno."
    }

    // Regex na datum
    private fun formatDateWithYear(raw: String): String? {
        val regex = Regex("""^(\d{1,2})\.(\d{1,2})\.?$""")
        val match = regex.matchEntire(raw.trim()) ?: return null

        val day = match.groupValues[1].toInt()
        val month = match.groupValues[2].toInt()

        if (month !in 1..12) return null
        val maxDayInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> 29
            else -> 0
        }
        if (day !in 1..maxDayInMonth) return null

        val dd = day.toString().padStart(2, '0')
        val mm = month.toString().padStart(2, '0')
        val fixedYear = "2024"

        return "$fixedYear-$mm-$dd"
    }
}
