package com.rober.trashlocator.utils

import android.content.Context
import android.content.res.Configuration
import java.util.*

object LocaleManager {
    fun setLocaleLanguage(newBase: Context?): Context? {
        val sharedPreferences = newBase?.applicationContext?.getSharedPreferences(
            newBase.packageName + "_preferences",
            Context.MODE_PRIVATE
        ) ?: return newBase

        val lang = sharedPreferences.getString(Constants.CURRENT_LANGUAGE, "en")!!

        val locale = Locale(lang)
        val config = Configuration(newBase.resources.configuration)
        Locale.setDefault(locale)
        config.setLocale(locale)

        return newBase.createConfigurationContext(config)
    }
}