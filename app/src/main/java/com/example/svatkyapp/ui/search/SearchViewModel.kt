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
import com.example.svatkyapp.data.FavoriteRepository

class SearchViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

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

    fun addFavoriteFromInputName(
        onError: (String) -> Unit = {},
        onSuccess: (String) -> Unit = {},
        onAlreadyExists: (String) -> Unit = {}
    ) {
        val rawName = inputName.trim()
        if (rawName.isBlank()) {
            onError("Zadejte prosím jméno.")
            return
        }

        val dates = namedays[rawName.lowercase()]
        if (dates.isNullOrEmpty()) {
            onError("Tohle jméno jsem nenašla v kalendáři.")
            return
        }

        val addedAny = dates.mapNotNull { rawDayMonth ->
            val displayDate = buildDisplayDate(rawDayMonth) ?: return@mapNotNull null
            val favKey = "$displayDate|$rawName"
            if (favoriteRepository.isFavorite(favKey)) {
                null
            } else {
                favoriteRepository.toggleFavorite(favKey)
                favKey
            }
        }

        if (addedAny.isEmpty()) {
            onAlreadyExists("$rawName už je v oblíbených")
        } else {
            onSuccess("$rawName byl přidán do oblíbených")
        }
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

    private fun buildDisplayDate(dayMonth: String, year: String = "2025"): String? {
        // očekává "14.08." nebo "14.08"
        val regex = Regex("""^(\d{1,2})\.(\d{1,2})\.?$""")
        val m = regex.matchEntire(dayMonth.trim()) ?: return null

        val day = m.groupValues[1].toInt()
        val month = m.groupValues[2].toInt()

        // validace dnů v měsíci
        val maxDay = when (month) {
            1,3,5,7,8,10,12 -> 31
            4,6,9,11 -> 30
            2 -> 29
            else -> return null
        }
        if (day !in 1..maxDay) return null

        val dd = day.toString().padStart(2, '0')
        val mm = month.toString().padStart(2, '0')
        return "$dd.$mm.$year"
    }
}
