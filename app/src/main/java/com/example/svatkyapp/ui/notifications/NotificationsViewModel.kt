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

    // Zavolej to vždy, když chceš načíst/obnovit data
    fun loadFavorites() {
        // favoriteRepository ukládá klíče typu "01.10.2025|Igor"
        val data = favoriteRepository
            .getAllFavorites()
            .map { key ->
                val parts = key.split("|")
                val date = parts.getOrNull(0) ?: ""
                val name = parts.getOrNull(1) ?: ""
                FavoriteItem(date = date, name = name)
            }

        _favorites.value = data
    }
}

// jednoduchý datový model pro zobrazení ve Favorites
data class FavoriteItem(
    val date: String,
    val name: String
)
