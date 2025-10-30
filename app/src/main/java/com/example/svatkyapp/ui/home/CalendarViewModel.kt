package com.example.svatkyapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.svatkyapp.data.NamedayApiService
import kotlinx.coroutines.launch

data class DayEntry(
    val dateDisplay: String, // "DD.MM.YYYY"
    val name: String         // "Martin"
)

class HomeViewModel : ViewModel() {

    // "Svátek má: ___"
    val namedayToday = MutableLiveData("...")

    // Režim kalendáře
    val showRowView = MutableLiveData(false)

    val rowNamedays = MutableLiveData<List<DayEntry>>(emptyList())
    private val czFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadTodayNameday()
        loadLinearNamedays(LocalDate.now().monthValue)
    }

    fun toggleView() {
        val newState = !(showRowView.value ?: false)
        showRowView.value = newState
    }

    fun loadTodayNameday() {
        viewModelScope.launch {
            val nameFromApi = NamedayApiService.getNamedayToday()
            namedayToday.postValue(nameFromApi ?: "Nenalezeno")
        }
    }

    fun onDateSelected(date: LocalDate) {
        val formatted = date.format(apiFormatter) // např. "2025-06-14"
        viewModelScope.launch {
            val nameFromApi = NamedayApiService.getNamedayForDate(formatted)
            namedayToday.postValue(nameFromApi ?: "Nenalezeno")
        }
    }

    fun loadLinearNamedays(month: Int) {
        val year = LocalDate.now().year
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

        viewModelScope.launch {
            val list = mutableListOf<DayEntry>()

            for (day in 1..daysInMonth) {
                val d = LocalDate.of(year, month, day)
                val dateDisplay = d.format(czFormatter)
                val formatted = d.format(DateTimeFormatter.ISO_DATE) // "2025-10-22"

                // načteme jméno z API
                val apiName = NamedayApiService.getNamedayForDate(formatted)
                val name = apiName?.takeIf { it.isNotEmpty() } ?: "Nenalezeno"

                list.add(DayEntry(dateDisplay, name))
            }

            // aktualizuj seznam po načtení všech dní
            rowNamedays.postValue(list)
        }
    }

    fun monthNameCz(month: Int): String {
        return listOf(
            "Leden","Únor","Březen","Duben","Květen","Červen",
            "Červenec","Srpen","Září","Říjen","Listopad","Prosinec"
        )[month - 1]
    }
}

