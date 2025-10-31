package com.example.svatkyapp

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleHelper {

    fun wrapContext(base: Context, lang: String = "cs", country: String = "CZ"): Context {
        val locale = Locale(lang, country)
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        // nastavíme locale do configu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            config.setLayoutDirection(locale)
            return base.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            base.resources.updateConfiguration(config, base.resources.displayMetrics)
            return base
        }
    }
}
