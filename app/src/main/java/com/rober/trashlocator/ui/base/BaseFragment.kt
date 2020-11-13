package com.rober.trashlocator.ui.base

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import com.rober.trashlocator.ui.MapsActivity


abstract class BaseFragment<VM : ViewModel>(view: Int) : Fragment(view) {

    abstract val viewModel: VM
    lateinit var mapsActivity: MapsActivity

    private val TAG = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "On Create")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "On View Created")
        setupListeners()
        setupView()
        mapsActivity = requireActivity() as MapsActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "On Create View")
        return super.onCreateView(inflater, container, savedInstanceState)
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
        if (mapsActivity.findNavHostFragment().childFragmentManager.backStackEntryCount == 0) {
            Log.d("BaseFragment", "Move task to back")
            requireActivity().moveTaskToBack(true)
        } else {
            Log.d("BaseFragment", "PopBackStack")
            findNavController().popBackStack()
        }
    }

    fun getColor(color: Int): Int {
        return ContextCompat.getColor(requireContext(), color)
    }

    fun isNightMode(): Boolean {
        return mapsActivity.isNightModeSet()
    }


    override fun onResume() {
        super.onResume()
        Log.i(TAG, "On Resume")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "On Stop")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "On Pause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "On Resume")
    }
}