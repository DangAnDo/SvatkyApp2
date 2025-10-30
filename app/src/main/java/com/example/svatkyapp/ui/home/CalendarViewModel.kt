package com.example.svatkyapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class HomeViewModel : ViewModel() {

    // "Svátek má: ___"
    val namedayToday = MutableLiveData("...")

    init {
        loadTodayNameday()
    }

    fun loadTodayNameday() {
        // TODO: později nahradíme voláním API getTodayNameday()
        namedayToday.value = "Martin"
    }

    fun onDateSelected(date: LocalDate) {
        // TODO: tady pak použijeme API getNamedayForDate(dateISO)
        namedayToday.value = "Jméno pro ${date.dayOfMonth}. den"
    }
}
