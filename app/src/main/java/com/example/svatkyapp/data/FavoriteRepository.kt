package com.example.svatkyapp.data

import android.content.Context
import androidx.core.content.edit

class FavoriteRepository(context: Context) {

    private val prefs = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val keyset = "favorite_keys"

    private fun getSet(): MutableSet<String> {
        return prefs.getStringSet(keyset, emptySet())?.toMutableSet() ?: mutableSetOf()
    }

    fun isFavorite(key: String): Boolean {
        return getSet().contains(key)
    }

    // přepne stav a VRÁTÍ nový stav
    fun toggleFavorite(key: String): Boolean {
        val set = getSet()
        val nowFavorite: Boolean
        if (set.contains(key)) {
            set.remove(key)
            nowFavorite = false
        } else {
            set.add(key)
            nowFavorite = true
        }
        prefs.edit {
            putStringSet(keyset, set)
        }
        return nowFavorite
    }
    fun getAllFavorites(): Set<String> {
        return getSet()
    }

    fun removeFavorite(key: String) {
        val set = getSet()
        set.remove(key)
        prefs.edit {
            putStringSet(keyset, set)
        }
    }
}
