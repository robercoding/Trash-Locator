package com.rober.trashlocator.ui.fragments.about

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.AboutAppFragmentBinding
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding
import com.rober.trashlocator.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.about_app_fragment.*

@AndroidEntryPoint
class AboutAppFragment : BaseFragment<AboutAppViewModel>(R.layout.about_app_fragment) {

    override val viewmodel: AboutAppViewModel by viewModels()
    private val binding: AboutAppFragmentBinding by viewBinding(AboutAppFragmentBinding::bind)
    private var easterEgg = false
    private var countClick = 10
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_app_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPreferences = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        easterEgg = sharedPreferences.getBoolean(Constants.EASTER_EGG, false)
    }

    override fun setupListeners() {
        super.setupListeners()
        textVersion.setOnClickListener {
            if (easterEgg) {
                displayToast("You already found the easter egg! ${String(Character.toChars(0x1F60A))}")
            } else {
                if (countClick > 0) {
                    toast?.cancel()
                    toast = Toast.makeText(
                        requireContext(),
                        "You are ${countClick} steps away to find something!",
                        Toast.LENGTH_SHORT
                    )
                    toast?.show()
                    countClick--
                } else {
                    toast?.cancel()
                    easterEgg = true
                    saveEasterEggInSharedPreferences(easterEgg)
                    displayToast("You found the easter egg! ${String(Character.toChars(0x1F60A))}")
                }
            }
        }
    }

    private fun saveEasterEggInSharedPreferences(value: Boolean) {
        val sharedPreferences = requireContext().applicationContext.getSharedPreferences(
            requireContext().packageName + "_preferences",
            Context.MODE_PRIVATE
        )
        val sharedPreferencesEdit = sharedPreferences.edit()
        sharedPreferencesEdit.putBoolean(Constants.EASTER_EGG, value)
        sharedPreferencesEdit.apply()
    }
}