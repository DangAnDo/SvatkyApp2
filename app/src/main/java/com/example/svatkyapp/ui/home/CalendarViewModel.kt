package com.example.svatkyapp.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.svatkyapp.data.NamedayApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class DayEntry(
    val dateDisplay: String,
    val name: String
)

class HomeViewModel : ViewModel() {

    // "Svátek má: ___"
    val namedayToday = MutableLiveData("...")

    // Režim kalendáře
    val showRowView = MutableLiveData(false)

    val rowNamedays = MutableLiveData<List<DayEntry>>(emptyList())
    @RequiresApi(Build.VERSION_CODES.O)
    private val czFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    @RequiresApi(Build.VERSION_CODES.O)
    private val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private var loadMonthJob: Job? = null

    init {
        loadTodayNameday()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            loadLinearNamedays(LocalDate.now().monthValue)
        }
    }

    fun loadTodayNameday() {
        viewModelScope.launch {
            val nameFromApi = NamedayApiService.getNamedayToday()
            namedayToday.postValue(nameFromApi ?: "Nenalezeno")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDateSelected(date: LocalDate) {
        val formatted = date.format(apiFormatter) // např. "2025-06-14"
        viewModelScope.launch {
            val nameFromApi = NamedayApiService.getNamedayForDate(formatted)
            namedayToday.postValue(nameFromApi ?: "Nenalezeno")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadLinearNamedays(month: Int) {
        val year = LocalDate.now().year
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

        // vyčisti seznam nebo zruší načítání
        rowNamedays.value = emptyList()
        loadMonthJob?.cancel()

        loadMonthJob = viewModelScope.launch {
            val dayJobs = (1..daysInMonth).map { day ->
                async {
                    val d = LocalDate.of(year, month, day)
                    val displayDate = d.format(czFormatter)
                    val isoDate = d.format(DateTimeFormatter.ISO_DATE)
                    val apiName = NamedayApiService.getNamedayForDate(isoDate)
                    val finalName = apiName?.takeIf { it.isNotEmpty() } ?: "Nenalezeno"
                    DayEntry(dateDisplay = displayDate, name = finalName)
                }
            }
            val allDays = dayJobs.awaitAll()
            rowNamedays.postValue(allDays)
        }
    }

    fun monthNameCz(month: Int): String {
        return listOf(
            "Leden","Únor","Březen","Duben","Květen","Červen",
            "Červenec","Srpen","Září","Říjen","Listopad","Prosinec"
        )[month - 1]
    }
}

