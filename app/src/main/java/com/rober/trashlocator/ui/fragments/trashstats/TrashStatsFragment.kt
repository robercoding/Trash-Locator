package com.rober.trashlocator.ui.fragments.trashstats

import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.rober.trashlocator.R
import com.rober.trashlocator.databinding.TrashStatsFragmentBinding
import com.rober.trashlocator.ui.base.BaseFragment
import com.rober.trashlocator.ui.base.viewBinding
import com.rober.trashlocator.utils.LocalitiesDataset

class TrashStatsFragment : BaseFragment<TrashStatsViewModel>(R.layout.trash_stats_fragment) {

    override val viewModel: TrashStatsViewModel by viewModels()
    private val binding: TrashStatsFragmentBinding by viewBinding(TrashStatsFragmentBinding::bind)

    override fun setupView() {
        super.setupView()
        val listTrashQuantity = LocalitiesDataset.listTrashQuantity

        val listPieEntry = mutableListOf<PieEntry>()
        for (localityTrashQuantity in listTrashQuantity) {
            val pieEntry =
                PieEntry(localityTrashQuantity.trashQuantity, localityTrashQuantity.locality)
            listPieEntry.add(pieEntry)
        }

        val pieDataSet = PieDataSet(listPieEntry, "")
        pieDataSet.colors = mutableListOf(
            getColor(R.color.orange),
            getColor(R.color.blueNormal),
            getColor(R.color.green)
        )

        //Custom piedataset and set to the view
        pieDataSet.valueTextColor = getColor(R.color.primaryText)
        pieDataSet.valueTextSize = 15f
        pieDataSet.isHighlightEnabled = true
        pieDataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

        binding.piechart.data = PieData(pieDataSet)

        binding.piechart.description.isEnabled = false
        binding.piechart.setDrawEntryLabels(true)
        binding.piechart.isDrawHoleEnabled = false

        //Modify piechart legend
        val legend = binding.piechart.legend
        legend.textSize = 13f
        legend.isWordWrapEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.yEntrySpace = 8f

        //Set color
        binding.piechart.setEntryLabelColor(getColor(R.color.primaryText))
        binding.piechart.legend.textColor = getColor(R.color.primaryText)
    }
}