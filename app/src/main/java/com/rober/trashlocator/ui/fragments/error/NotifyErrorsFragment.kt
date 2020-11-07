package com.rober.trashlocator.ui.fragments.error

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.NotifyErrorsFragmentBinding
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding

class NotifyErrorsFragment : BaseFragment<NotifyErrorsViewModel>(R.layout.notify_errors_fragment) {
    override val viewModel: NotifyErrorsViewModel by viewModels()
    private val binding: NotifyErrorsFragmentBinding by viewBinding(NotifyErrorsFragmentBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notify_errors_fragment, container, false)
    }
}