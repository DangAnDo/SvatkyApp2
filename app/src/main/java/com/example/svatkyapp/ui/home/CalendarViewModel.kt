package com.example.svatkyapp.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

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

    init {
        loadTodayNameday()
        loadLinearNamedays(LocalDate.now().monthValue)
    }

    fun toggleView() {
        val newState = !(showRowView.value ?: false)
        showRowView.value = newState
    }

    fun loadTodayNameday() {
        // TODO: později nahradíme voláním API getTodayNameday()
        namedayToday.value = "Martin"
    }

    fun onDateSelected(date: LocalDate) {
        // TODO: tady pak použijeme API getNamedayForDate(dateISO)
        namedayToday.value = "Jméno pro ${date.dayOfMonth}. den"
    }

    fun loadLinearNamedays(month: Int) {
        val year = LocalDate.now().year
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()

        val list = mutableListOf<DayEntry>()
        for (day in 1..daysInMonth) {
            val d = LocalDate.of(year, month, day)
            val dateDisplay = d.format(czFormatter)
            val mockName = "Jméno $day" // TODO: nahradíme API daty

            list.add(DayEntry(dateDisplay, mockName))
        }

        // výsledek seřadíme podle data (už je to ve stoupajícím pořadí, takže není nutné sortit)
        rowNamedays.value = list
    }

    fun monthNameCz(month: Int): String {
        return listOf(
            "Leden","Únor","Březen","Duben","Květen","Červen",
            "Červenec","Srpen","Září","Říjen","Listopad","Prosinec"
        )[month - 1]
    }
}

