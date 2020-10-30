package com.rober.papelerasvalencia.ui.base

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment


abstract class BaseFragment(private val view: Int): Fragment(view){

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListeners()
        setupView()
    }

    open fun setupListeners(){

    }

    open fun setupView(){

    }

    fun displayToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}