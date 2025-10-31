package com.example.svatkyapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.svatkyapp.R
import com.example.svatkyapp.data.FavoriteRepository

class NotificationsFragment : Fragment() {

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val root = inflater.inflate(R.layout.fragment_notifications, container, false)

        // najdeme view prvky z layoutu
        recyclerFavorites = root.findViewById(R.id.recycler_favorites)
        emptyText = root.findViewById(R.id.text_empty)

        // repo -> viewmodel
        val favoriteRepository = FavoriteRepository(requireContext())
        viewModel = NotificationsViewModel(favoriteRepository)

        // recycler view setup
        adapter = FavoriteAdapter(emptyList())
        recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        recyclerFavorites.adapter = adapter

        // pozorujeme data z viewmodelu
        viewModel.favorites.observe(viewLifecycleOwner) { favoriteItems ->
            if (favoriteItems.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                recyclerFavorites.visibility = View.GONE
            } else {
                emptyText.visibility = View.GONE
                recyclerFavorites.visibility = View.VISIBLE
                adapter.updateData(favoriteItems)
            }
        }

        // načti data (to si vytáhne věci ze SharedPreferences)
        viewModel.loadFavorites()

        return root
    }

    // Adapter pro oblíbené položky
    class FavoriteAdapter(
        private var items: List<FavoriteItem>
    ) : RecyclerView.Adapter<FavoriteAdapter.FavViewHolder>() {

        fun updateData(newItems: List<FavoriteItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite, parent, false) as ViewGroup
            return FavViewHolder(view)
        }

        override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class FavViewHolder(private val root: ViewGroup) : RecyclerView.ViewHolder(root) {
            private val textDate: TextView = root.findViewById(R.id.text_fav_date)
            private val textName: TextView = root.findViewById(R.id.text_fav_name)

            fun bind(item: FavoriteItem) {
                textDate.text = item.date
                textName.text = item.name
            }
        }
    }
}
