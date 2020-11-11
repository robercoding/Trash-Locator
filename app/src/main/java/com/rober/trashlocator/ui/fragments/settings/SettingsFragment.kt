package com.rober.trashlocator.ui.fragments.settings


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.jakewharton.processphoenix.ProcessPhoenix
import com.rober.trashlocator.R
import com.rober.trashlocator.ui.MapsActivity
import com.rober.trashlocator.utils.Constants
import com.rober.trashlocator.utils.Destinations

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var listLanguage: ListPreference
    private lateinit var switchTheme: SwitchPreferenceCompat
    private lateinit var sharedPreference: SharedPreferences

    private var lang = "en"
    private val listLocales = listOf("en", "es")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeVariables()
        setupListeners()
        fillListLanguage()
    }

    private fun initializeVariables() {
        listLanguage = findPreference<ListPreference>(Constants.KEY_LIST_LANGUAGE)!!
        switchTheme = findPreference(Constants.KEY_SWITCH_THEME)!!

        sharedPreference = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }

    //Convert a string array to charsequence array and apply to list language of preference fragment
    private fun fillListLanguage() {
        val stringArray = requireContext().resources.getStringArray(R.array.list_languages)

        val listEntriesCharSequence = mutableListOf<CharSequence>()
        for (string in stringArray) {
            listEntriesCharSequence.add(string)
        }
        val arrayEntriesCharSequence = listEntriesCharSequence.toTypedArray()

        listLanguage.entries = arrayEntriesCharSequence
        listLanguage.entryValues = arrayEntriesCharSequence
        listLanguage.setValueIndex(setValueListLanguage())
    }

    private fun setValueListLanguage(): Int {
        val sharedPreferences = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        lang = sharedPreferences.getString(Constants.CURRENT_LANGUAGE, "en")!!

        return when (lang) {
            "en" -> 0
            "es" -> 1
            else -> 0
        }
    }

    private fun findLocale(language: String) {
        val languagesValues = requireContext().resources.getStringArray(R.array.list_languages)
        val selectedLanguageValue = language

        var locale = ""
        var index = 0
        for (languageValue in languagesValues) {
            if (languageValue == selectedLanguageValue) {
                locale = listLocales[index]
                break
            }
            index++
        }

        if (locale == lang) {
            return
        }

        if (locale.isNotBlank()) {
            setLocale(locale)
        }
    }

    private fun setLocale(locale: String) {
        val sharedPreferenceEditor = sharedPreference.edit()
        sharedPreferenceEditor.putString(Constants.CURRENT_LANGUAGE, locale)
        sharedPreferenceEditor.apply()
        ProcessPhoenix.triggerRebirth(requireContext())
    }

    private fun changeTheme(darkTheme: Boolean) {
        val sharedPreferenceEditor = sharedPreference.edit()
        sharedPreferenceEditor.putBoolean(Constants.CURRENT_THEME, darkTheme)
        sharedPreferenceEditor.apply()

        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
//        requireActivity().recreate()
//        findNavController().navigate(R.id.settingsFragment)
//        if(sharedPreference.getBoolean(Constants.CURRENT_THEME, false) == darkTheme){
//            ProcessPhoenix.triggerRebirth(requireContext())
//        }else{
//
//        }
    }

    private fun setupListeners() {
        listLanguage.setOnPreferenceChangeListener { preference, language ->
            findLocale(language as String)
            true
        }

        switchTheme.setOnPreferenceChangeListener { preference, newValue ->
            changeTheme(newValue as Boolean)
            true
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    (requireActivity() as MapsActivity).currentDestinationId =
                        Destinations.mapsFragment
                    findNavController().popBackStack()
                }
            })
    }
}