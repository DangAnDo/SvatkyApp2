package com.example.svatkyapp.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object NamedayApiService {

    // dnešní svátek: https://svatkyapi.cz/api/day
    suspend fun getNamedayToday(): String? = withContext(Dispatchers.IO) {
        fetchAndParse("https://svatkyapi.cz/api/day")
    }

    // svátek pro konkrétní den: https://svatkyapi.cz/api/day/2025-10-22
    suspend fun getNamedayForDate(date: String): String? = withContext(Dispatchers.IO) {
        fetchAndParse("https://svatkyapi.cz/api/day/$date")
    }

    private fun fetchAndParse(url: String): String? {
        return try {
            val response = URL(url).readText()

            Log.d("NamedayApiService", "GET $url -> $response")
            val obj = JSONObject(response)
            val name = obj.optString("name", "")
            name.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.e("NamedayApiService", "error loading $url", e)
            null
        }
    }
}
