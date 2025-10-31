package com.example.svatkyapp

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun wrapContext(base: Context, lang: String = "cs", country: String = "CZ"): Context {
        val locale = Locale.Builder().setLanguage(lang).setRegion(country).build()
        Locale.setDefault(locale)

        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return base.createConfigurationContext(config)
    }
}
