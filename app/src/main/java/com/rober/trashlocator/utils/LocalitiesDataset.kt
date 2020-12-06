package com.rober.trashlocator.utils

import com.rober.trashlocator.R
import com.rober.trashlocator.models.LocalityDataSet
import com.rober.trashlocator.models.LocalityTrashQuantity

object LocalitiesDataset {
    val listLocalityDataset = listOf<LocalityDataSet>(
        LocalityDataSet(
            "Santa Cruz de Tenerife",
            "Canary Islands,Canarias",
            R.raw.trash_santa_cruz_de_tenerife
        ),
        LocalityDataSet("Washington", "District of Columbia", R.raw.trash_washingon_dc),
        LocalityDataSet("Cáceres\u200E", "Extremadura", R.raw.trash_caceres)
    )

    val listTrashQuantity = listOf<LocalityTrashQuantity>(
        LocalityTrashQuantity("Santa Cruz de Tenerife", 6955f),
        LocalityTrashQuantity("Washington, DC", 5265f),
        LocalityTrashQuantity("Cáceres", 4050f)
    )
}