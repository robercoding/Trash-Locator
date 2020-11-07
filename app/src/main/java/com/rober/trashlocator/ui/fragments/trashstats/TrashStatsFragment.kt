package com.rober.trashlocator.ui.fragments.trashstats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.TrashStatsFragmentBinding
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding

class TrashStatsFragment : BaseFragment<TrashStatsViewModel>(R.layout.trash_stats_fragment) {
    override val viewModel: TrashStatsViewModel by viewModels()
    private val binding: TrashStatsFragmentBinding by viewBinding(TrashStatsFragmentBinding::bind)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.trash_stats_fragment, container, false)
    }
}