package com.example.svatkyapp.ui.calendar

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
import com.example.svatkyapp.R
import java.time.LocalDate
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CalendarFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var blockCalendar: LinearLayout
    private lateinit var blockList: LinearLayout
    private lateinit var datePicker: DatePicker
    private lateinit var textNamedayValue: TextView
    private lateinit var btnShowList: Button
    private lateinit var btnShowCalendar: Button
    private lateinit var monthButtonsContainer: LinearLayout
    private lateinit var recyclerDays: RecyclerView
    private lateinit var adapter: DayEntryAdapter

    @RequiresApi(Build.VERSION_CODES.O) // kvůli java.time.LocalDate
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val root = inflater.inflate(R.layout.fragment_calendar, container, false)
        blockCalendar = root.findViewById(R.id.block_calendar_mode)
        blockList = root.findViewById(R.id.block_list_mode)
        datePicker = root.findViewById(R.id.date_picker)
        textNamedayValue = root.findViewById(R.id.text_nameday_value)
        btnShowList = root.findViewById(R.id.btn_show_list)
        btnShowCalendar = root.findViewById(R.id.btn_show_calendar)
        monthButtonsContainer = root.findViewById(R.id.month_buttons_container)
        recyclerDays = root.findViewById(R.id.recycler_days)

        adapter = DayEntryAdapter()
        recyclerDays.layoutManager = LinearLayoutManager(requireContext())
        recyclerDays.adapter = adapter

        // Když uživatel změní datum v kalendáři
        datePicker.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            val selected = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
            viewModel.onDateSelected(selected)
        }

        setupMonthButtons()

        // přepnout do řádkového kalendáře (měsíce + seznam)
        btnShowList.setOnClickListener {
            viewModel.showRowView.value = true
        }

        // přepnout zpět na klasický kalendář
        btnShowCalendar.setOnClickListener {
            viewModel.showRowView.value = false
        }

        // Posloucháme LiveData z ViewModelu a zobrazíme text
        viewModel.namedayToday.observe(viewLifecycleOwner) { name ->
            textNamedayValue.text = name ?: "..."
        }
        viewModel.rowNamedays.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        // přepínání zobrazení mezi "klasický kalendář" a "řádkový kalendář"
        viewModel.showRowView.observe(viewLifecycleOwner) { showRow ->
            if (showRow == true) {
                // řádkový kalendář zapnout
                blockCalendar.visibility = View.GONE
                blockList.visibility = View.VISIBLE
            } else {
                // klasický kalendář zpět
                blockCalendar.visibility = View.VISIBLE
                blockList.visibility = View.GONE
            }
        }
        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupMonthButtons() {
        // smažeme stará tlačítka, kdyby se to volalo víckrát
        monthButtonsContainer.removeAllViews()

        val density = resources.displayMetrics.density
        val minTouchSizePx = (48 * density).toInt()

        for (m in 1..12) {
            val btn = Button(requireContext()).apply {
                text = viewModel.monthNameCz(m).uppercase()
                textSize = 14f
                setPadding(32, 20, 32, 20)

                // černé tlačítko s bílým textem
                setBackgroundColor(0xFF000000.toInt())
                setTextColor(0xFFFFFFFF.toInt())

                minWidth = minTouchSizePx
                minHeight = minTouchSizePx

                // mezery mezi tlačítky
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(16, 8, 16, 8)
                layoutParams = params

                setOnClickListener {
                    viewModel.loadLinearNamedays(m)
                    viewModel.showRowView.value = true
                }
            }

            monthButtonsContainer.addView(btn)
        }
    }
}
