package com.example.svatkyapp.ui.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.svatkyapp.R
import com.example.svatkyapp.data.FavoriteRepository

class DayEntryAdapter(private val favoriteRepository: FavoriteRepository) : RecyclerView.Adapter<DayEntryAdapter.DayEntryViewHolder>() {

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
        fun bind(item: DayEntry) {
            textDate.text = item.dateDisplay
            textName.text = item.name

            // Unikátní klíč pro ten řádek
            val favKey = makeFavoriteKey(item)

            // Ikonka podle uloženého stavu
            updateIcon(favoriteRepository.isFavorite(favKey))

            favButton.setOnClickListener {
                // Uložení
                val newState = favoriteRepository.toggleFavorite(favKey)
                updateIcon(newState)
                animateFavorite()
            }
        }
        private fun updateIcon(isFavorite: Boolean) {
            favButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorites
                else R.drawable.ic_person_heart
            )
        }
        private fun animateFavorite() {
            favButton.scaleX = 0.8f
            favButton.scaleY = 0.8f
            favButton.alpha = 0.6f

            favButton.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .alpha(1f)
                .setDuration(120)
                .withEndAction {
                    favButton.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(80)
                        .start()
                }
                .start()
        }
    }
}

private fun makeFavoriteKey(item: DayEntry): String {
    // důležité: musí být stejné vždy pro stejný den/jméno,
    // aby se to po restartu našlo
    return "${item.dateDisplay}|${item.name}"
}
