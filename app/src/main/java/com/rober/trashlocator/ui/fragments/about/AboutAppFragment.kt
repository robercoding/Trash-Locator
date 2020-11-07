package com.rober.trashlocator.ui.fragments.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.AboutAppFragmentBinding
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding

class AboutAppFragment : BaseFragment<AboutAppViewModel>(R.layout.about_app_fragment) {

    override val viewModel: AboutAppViewModel by viewModels()
    private val binding: AboutAppFragmentBinding by viewBinding(AboutAppFragmentBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.about_app_fragment, container, false)
    }
}