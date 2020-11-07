package com.rober.trashlocator.ui.fragments.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.rober.trashlocator.R
import com.rober.trashlocator.utils.Constants

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
        loadListLanguage()
    }

    private fun loadListLanguage() {
        val listLanguage = findPreference<ListPreference>(Constants.KEY_LIST_LANGUAGE)!!
        val entries = arrayOf<CharSequence>("English", "Spanish")
        //Investigate to pass string array to char array
//        val entries = arrayOf<CharSequence>(context.resources.getStringArray(R.array.list_languages))
        listLanguage.entries = entries
        listLanguage.entryValues = entries
    }

    private fun findLocale(language: String) {
        //TODO
    }

    private fun setLocale(locale: String) {
        //TODO
    }
}