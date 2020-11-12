package com.rober.trashlocator.ui.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.rober.trashlocator.ui.MapsActivity


abstract class BaseFragment<VM : ViewModel>(view: Int) : Fragment(view) {

    abstract val viewModel: VM
    lateinit var mapsActivity: MapsActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupView()
        mapsActivity = requireActivity() as MapsActivity
    }

    open fun setupListeners() {
        detectOnBackPressed()
    }

    open fun setupView() {}

    fun hideKeyBoard() {
        val imm: InputMethodManager =
            context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    fun displayToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    open fun detectOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    defaultOnBackPressed()
                }
            })
    }

    fun defaultOnBackPressed() {
        /*
         * if NavHost backstackentrycount == 0 means that there are no other fragments behind the current one!
         * NavController backstack returns 2 if there are no other fragments and it's more confusing since it
         * refers to Activity and the fragment itself so I didn't choose that option.
         */
        if (mapsActivity.navHostFragment.childFragmentManager.backStackEntryCount == 0) {
            Log.d("BaseFragment", "Move task to back")
            requireActivity().moveTaskToBack(true)
        } else {
            Log.d("BaseFragment", "PopBackStack")
            findNavController().popBackStack()
        }
    }
}