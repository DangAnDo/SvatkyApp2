package com.example.svatkyapp.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.svatkyapp.R
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModels()

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
    }
}
