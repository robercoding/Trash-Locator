package com.rober.papelerasvalencia.utils

import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.models.LocalityDataset

object LocalitesDataset {
    val listLocalityDataset = listOf<LocalityDataset>(
        LocalityDataset(
            "Santa Cruz de Tenerife",
            "Canary Islands,Canarias",
            R.raw.trash_santa_cruz_de_tenerife
        ),
        LocalityDataset("Washington", "District of Columbia", R.raw.trash_washingon_dc),
        LocalityDataset("Cáceres\u200E", "Extremadura", R.raw.trash_caceres)
    )
}