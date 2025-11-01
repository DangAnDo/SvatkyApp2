package com.example.svatkyapp.ui.favorites

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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
class FavoritesFragment : Fragment() {

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: FavoriteAdapter
    private lateinit var favoriteRepository: FavoriteRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        val root = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerFavorites = root.findViewById(R.id.recycler_favorites)
        emptyText = root.findViewById(R.id.text_empty)
        favoriteRepository = FavoriteRepository(requireContext())
        viewModel = NotificationsViewModel(favoriteRepository)

        adapter = FavoriteAdapter(
            onLongPressRemove = { item ->
                viewModel.removeFavorite(item)
            }
        )

        recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        recyclerFavorites.adapter = adapter

        viewModel.favorites.observe(viewLifecycleOwner) { favoriteItems ->
            if (favoriteItems.isEmpty()) {
                emptyText.visibility = View.VISIBLE
                recyclerFavorites.visibility = View.GONE
            } else {
                emptyText.visibility = View.GONE
                recyclerFavorites.visibility = View.VISIBLE
                adapter.submitList(favoriteItems)
            }
        }

        // načti data (to si vytáhne věci ze SharedPreferences)
        viewModel.loadFavorites()

        return root
    }

    // Adapter pro oblíbené položky
    class FavoriteAdapter(
        private val onLongPressRemove: (FavoriteItem) -> Unit
    ) : ListAdapter<FavoriteItem, FavoriteAdapter.FavViewHolder>(DiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_favorite, parent, false) as ViewGroup
            return FavViewHolder(view, onLongPressRemove)
        }

        override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        class FavViewHolder(
            private val root: ViewGroup,
            private val onLongPressRemove: (FavoriteItem) -> Unit
        ) : RecyclerView.ViewHolder(root) {

            private val textDate: TextView = root.findViewById(R.id.text_fav_date)
            private val textName: TextView = root.findViewById(R.id.text_fav_name)

            fun bind(item: FavoriteItem) {
                textDate.text = item.date
                textName.text = item.name

                // dlouhý stisk = odebrat z oblíbených
                root.setOnLongClickListener {
                    onLongPressRemove(item)
                    true
                }
            }
        }
        companion object DiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
            override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
                return oldItem.key == newItem.key
            }
            override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
