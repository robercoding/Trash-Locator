package com.rober.trashlocator.ui.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.rober.trashlocator.R
import com.rober.trashlocator.utils.Constants

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var listLanguage: ListPreference
    private val listLocales = listOf("en", "es")
    private val listCountries = listOf("EN", "ES")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
//        return inflater.inflate(R.layout.settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariables()
        setupListeners()
        loadListLanguage()
    }

    private fun initializeVariables() {
        listLanguage = findPreference<ListPreference>(Constants.KEY_LIST_LANGUAGE)!!
    }

    //Convert a string array to charsequence array and apply to list language of preference fragment
    private fun loadListLanguage() {
        val stringArray = requireContext().resources.getStringArray(R.array.list_languages)

        val listEntriesCharSequence = mutableListOf<CharSequence>()
        for (string in stringArray) {
            listEntriesCharSequence.add(string)
        }
        val arrayEntriesCharSequence = listEntriesCharSequence.toTypedArray()

        listLanguage.entries = arrayEntriesCharSequence
        listLanguage.entryValues = arrayEntriesCharSequence
    }

    private fun findLocale(language: String) {
        val languagesValues = requireContext().resources.getStringArray(R.array.list_languages)
        val selectedLanguageValue = language

        var locale = ""
        var country = ""
        var index = 0
        for (languageValue in languagesValues) {
            if (languageValue == selectedLanguageValue) {
                locale = listLocales[index]
                country = listCountries[index]
                break
            }
            index++
        }

        if (locale.isNotBlank() && country.isNotBlank()) {
            //TODO SET LOCALE AND RESTART
        }
    }

    private fun changeTheme() {

    }

    private fun setupListeners() {
        listLanguage.setOnPreferenceChangeListener { preference, language ->
            findLocale(language as String)
            true
        }
    }
}