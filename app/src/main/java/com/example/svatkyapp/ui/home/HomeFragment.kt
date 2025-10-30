package com.example.svatkyapp.ui.home

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.svatkyapp.R
import java.time.LocalDate

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var datePicker: DatePicker
    private lateinit var textNamedayValue: TextView

    @RequiresApi(Build.VERSION_CODES.O) // kvůli java.time.LocalDate
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        datePicker = root.findViewById(R.id.date_picker)
        textNamedayValue = root.findViewById(R.id.text_nameday_value)

        // Když uživatel změni datum v kalendáři
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val selected = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            viewModel.onDateSelected(selected)
        }

        // Posloucháme LiveData z ViewModelu a zobrazíme text
        viewModel.namedayToday.observe(viewLifecycleOwner, Observer { name ->
            textNamedayValue.text = name ?: "..."
        })

        return root
    }
}
