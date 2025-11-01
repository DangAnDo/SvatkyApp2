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
                val date = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                FavoriteItem(date = date, name = name, key = key)
            }

        _favorites.value = data
    }
    fun removeFavorite(item: FavoriteItem) {
        // smažeme z persistentní paměti
        favoriteRepository.removeFavorite(item.key)

        // znovu načteme seznam, aby se UI aktualizovalo
        loadFavorites()
    }
}

data class FavoriteItem(
    val date: String,
    val name: String,
    val key: String
)