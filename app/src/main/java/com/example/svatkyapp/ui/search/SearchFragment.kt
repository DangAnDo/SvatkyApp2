package com.example.svatkyapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.svatkyapp.R
import kotlinx.coroutines.launch
import android.app.Dialog
import java.util.Calendar
import com.example.svatkyapp.data.FavoriteRepository


class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels {
        object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                val repo = FavoriteRepository(requireContext())
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(repo) as T
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.loadNamedaysFromAssets(requireContext())

        val inputDate = view.findViewById<EditText>(R.id.inputDate)
        val buttonSearchByDate = view.findViewById<Button>(R.id.buttonSearchByDate)
        val textResultDate = view.findViewById<TextView>(R.id.textResultDate)
        val inputName = view.findViewById<EditText>(R.id.inputName)
        val buttonSearchByName = view.findViewById<Button>(R.id.buttonSearchByName)
        val textResultName = view.findViewById<TextView>(R.id.textResultName)
        val buttonPickDate = view.findViewById<ImageButton>(R.id.buttonPickDate)
        val buttonPerson = view.findViewById<ImageButton>(R.id.buttonPerson)

        buttonPickDate.setOnClickListener {
            showDayMonthPicker { selectedDateString ->
                inputDate.setText(selectedDateString)
                viewModel.inputDate = selectedDateString
                viewModel.searchByDate()
            }
        }

        // Podle data (API)
        buttonSearchByDate.setOnClickListener {
            viewModel.inputDate = inputDate.text.toString()
            viewModel.searchByDate()
        }

        // Výsledek
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resultDate.collect { result ->
                textResultDate.text = result ?: ""
            }
        }

        // Podle jména
        buttonSearchByName.setOnClickListener {
            viewModel.inputName = inputName.text.toString()
            viewModel.searchByName()
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resultName.collect { result ->
                textResultName.text = result ?: ""
            }
        }

        buttonPerson.setOnClickListener {
            viewModel.inputName = inputName.text.toString()

            viewModel.addFavoriteFromInputName(
                onError = { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                },
                onSuccess = { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                },
                onAlreadyExists = { msg ->
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
    private fun showDayMonthPicker(onPicked: (selectedDate: String) -> Unit) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.fragment_spinner_data)

        val pickerDay = dialog.findViewById<NumberPicker>(R.id.pickerDay)
        val pickerMonth = dialog.findViewById<NumberPicker>(R.id.pickerMonth)
        val btnOk = dialog.findViewById<Button>(R.id.btnConfirmDayMonth)

        // pomocná funkce na max počet dní podle měsíce
        fun maxDaysInMonth(month: Int): Int {
            return when (month) {
                1, 3, 5, 7, 8, 10, 12 -> 31
                4, 6, 9, 11 -> 30
                2 -> 29
                else -> 31
            }
        }

        // výchozí hodnoty = dnešní den a měsíc
        val cal = Calendar.getInstance()
        val startDay = cal.get(Calendar.DAY_OF_MONTH)
        val startMonth = cal.get(Calendar.MONTH) + 1

        // nastav Month picker
        pickerMonth.minValue = 1
        pickerMonth.maxValue = 12
        pickerMonth.value = startMonth
        pickerMonth.wrapSelectorWheel = true

        // nastav Day picker podle měsíce
        pickerDay.minValue = 1
        pickerDay.maxValue = maxDaysInMonth(startMonth)
        pickerDay.value = if (startDay <= pickerDay.maxValue) startDay else pickerDay.maxValue
        pickerDay.wrapSelectorWheel = true

        // když uživatel změní měsíc, přepočítáme max den
        pickerMonth.setOnValueChangedListener { _, _, newMonth ->
            val newMaxDay = maxDaysInMonth(newMonth)
            val currentDay = pickerDay.value

            pickerDay.maxValue = newMaxDay
            if (currentDay > newMaxDay) {
                pickerDay.value = newMaxDay
            }
        }

        btnOk.setOnClickListener {
            val chosenDay = pickerDay.value
            val chosenMonth = pickerMonth.value

            val dd = chosenDay.toString().padStart(2, '0')
            val mm = chosenMonth.toString().padStart(2, '0')
            val displayValue = "$dd.$mm."

            onPicked(displayValue)
            dialog.dismiss()
        }

        dialog.show()
    }
}
