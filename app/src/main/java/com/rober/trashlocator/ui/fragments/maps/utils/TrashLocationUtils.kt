package com.rober.trashlocator.ui.fragments.maps.utils

import android.content.Context
import android.util.Log
import com.rober.trashlocator.R
import com.rober.trashlocator.models.AddressLocation
import com.rober.trashlocator.models.TrashLocation
import com.rober.trashlocator.utils.LocalitiesDataset
import com.rober.trashlocator.utils.getStringResources

class TrashLocationUtils(private val context: Context) {

    fun addCommaTrashLocation(
        trashLocation: TrashLocation,
        context: Context
    ): TrashLocation {
        val isStreetNameAvailable = trashLocation.streetName.isNotBlank()
        val isFeatureNameAvailable = trashLocation.feature.isNotBlank()
        val isLocalityAvailable = trashLocation.locality.isNotBlank()

        if ((isStreetNameAvailable && isFeatureNameAvailable) || (isStreetNameAvailable && isLocalityAvailable)) {
            trashLocation.streetName = trashLocation.streetName + ", "
        }
        if (isFeatureNameAvailable && isLocalityAvailable) {
            trashLocation.feature = trashLocation.feature + ", "
        }

        if (!isStreetNameAvailable && !isFeatureNameAvailable) {
            trashLocation.streetName = context.getStringResources(R.string.trash_no_information)
            trashLocation.feature = ""
            trashLocation.locality = ""
        }
        return trashLocation
    }

    fun getDataset(addressLocation: AddressLocation): Int {
        var raw = -1

        Log.i("SeeAddress", "Trying to get -> $addressLocation")
        //Try to find the dataset in file Object LocalitiesDataset
        loopLocalityDataset@ for (localityDataset in LocalitiesDataset.listLocalityDataset) {
            //Some localities are with "" so they directly go to check the admin area
//            if (addressLocation.localityName != "") {
//                if (localityDataset.localityName != addressLocation.localityName) continue@loopLocalityDataset
//            }

            if (localityDataset.localityName != addressLocation.localityName) continue@loopLocalityDataset
            /*
             * Split by comma because admin areas can have
             * different names example = "Canary Islands" == "Canarias"
             */
            val adminAreas = localityDataset.localityAdmin.split(',')
            loopAdminArea@ for (adminArea in adminAreas) {
                if (adminArea == addressLocation.localityAdminAreaName) {
                    raw = localityDataset.dataset
                    break@loopLocalityDataset
                }
            }
        }
        return raw
    }
}