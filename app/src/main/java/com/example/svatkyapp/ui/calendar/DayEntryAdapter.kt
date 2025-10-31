package com.example.svatkyapp.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.svatkyapp.R

class DayEntryAdapter : RecyclerView.Adapter<DayEntryAdapter.DayEntryViewHolder>() {

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
    inner class DayEntryViewHolder(private val root: ViewGroup) :
        RecyclerView.ViewHolder(root) {
        private val textDate: TextView = root.findViewById(R.id.text_date)
        private val textName: TextView = root.findViewById(R.id.text_name)
        private val favButton: ImageButton = root.findViewById(R.id.button_favorite)
        private var isFavorite = false

        fun bind(item: DayEntry) {
            textDate.text = item.dateDisplay
            textName.text = item.name

            // Výchozí ikonka
            favButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorites
                else R.drawable.ic_person_heart
            )
            favButton.setOnClickListener {
                isFavorite = !isFavorite
                favButton.setImageResource(
                    if (isFavorite) R.drawable.ic_favorites
                    else R.drawable.ic_person_heart
                )
            }
        }
    }
}
