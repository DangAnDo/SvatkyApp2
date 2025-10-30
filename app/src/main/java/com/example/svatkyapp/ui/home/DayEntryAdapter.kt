package com.example.svatkyapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.svatkyapp.R

class DayEntryAdapter : RecyclerView.Adapter<DayEntryViewHolder>() {

    private var items: List<DayEntry> = emptyList()

    fun submitList(newItems: List<DayEntry>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayEntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dayentry, parent, false) as ViewGroup
        return DayEntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayEntryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

class DayEntryViewHolder(private val root: ViewGroup) : RecyclerView.ViewHolder(root) {
    private val textDate: TextView = root.findViewById(R.id.text_date)
    private val textName: TextView = root.findViewById(R.id.text_name)

    fun bind(item: DayEntry) {
        textDate.text = item.dateDisplay
        textName.text = item.name
    }
}
