package com.example.svatkyapp.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.svatkyapp.data.FavoriteRepository

class NotificationsViewModel(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _favorites = MutableLiveData<List<FavoriteItem>>()
    val favorites: LiveData<List<FavoriteItem>> = _favorites
    fun loadFavorites() {
        val data = favoriteRepository
            .getAllFavorites()
            .map { key ->
                val parts = key.split("|")
                val fullDate = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""

                // zobrazí jen den a měsíc
                val displayDate = fullDate.substring(0, 6)

                FavoriteItem(date = displayDate, name = name, key = key)
            }
            // Řazení
            .sortedWith(
                compareBy<FavoriteItem> {
                    val parts = it.key.split("|")[0].split(".")
                    parts.getOrNull(1)?.toIntOrNull() ?: 0 // měsíc
                }.thenBy {
                    val parts = it.key.split("|")[0].split(".")
                    parts.getOrNull(0)?.toIntOrNull() ?: 0 // den
                }
            )

        _favorites.value = data
    }

    fun removeFavorite(item: FavoriteItem) {
        favoriteRepository.removeFavorite(item.key)
        loadFavorites()
    }
}

data class FavoriteItem(
    val date: String,
    val name: String,
    val key: String
)
