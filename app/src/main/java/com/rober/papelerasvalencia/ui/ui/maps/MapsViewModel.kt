package com.rober.papelerasvalencia.ui.ui.maps

import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.models.AddressLocation
import com.rober.papelerasvalencia.models.Trash
import com.rober.papelerasvalencia.models.TrashLocation
import com.rober.papelerasvalencia.utils.Event
import com.rober.papelerasvalencia.utils.LocalitesDataset
import com.rober.papelerasvalencia.utils.Utils
import com.rober.papelerasvalencia.utils.getStringResources

class MapsViewModel : ViewModel() {

    lateinit var geoCoder: Geocoder

    //Set List of Trash Item for cluster manager
    private val _listTrash = MutableLiveData<List<Trash>>()
    val listTrash: LiveData<List<Trash>> get() = _listTrash

    //Set list of addresses location for search toolbar
    private val _listAddressesLocation = MutableLiveData<List<AddressLocation>>()
    val listAddressesLocation: LiveData<List<AddressLocation>> get() = _listAddressesLocation

    //Observe location to move camera
    private val _addressLocation = MutableLiveData<AddressLocation>()
    val addressLocation: LiveData<AddressLocation> get() = _addressLocation

    private val _onBackPressed = MutableLiveData<Boolean>()
    val onBackPressed: LiveData<Boolean> get() = _onBackPressed

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = _message

    init {
        _onBackPressed.value = false
    }

    private lateinit var googleMap: GoogleMap
    private var listLocations = mutableListOf<AddressLocation>()
    private var lastNameLocation = ""

    //Get list addresses of addresses by name location and set on MutableLiveData
    fun getListAddressesByName(nameLocation: String, context: Context) {
        if ((nameLocation == lastNameLocation) || nameLocation.isBlank()) {
            return
        }
        Log.i("SeeNameLocation", nameLocation)
        lastNameLocation = nameLocation

        geoCoder = Geocoder(context)

        val addresses = geoCoder.getFromLocationName(nameLocation, 5)
        Log.i("SeeQuantity", "${addresses.size}")
        val mutableListAddressesLocation = mutableListOf<AddressLocation>()
        for (address in addresses) {
            Log.i("SeeAddress", "$address")
            val location = AddressLocation()

            val addressLine = address.getAddressLine(0)

            location.streetName = addressLine
            location.location.latitude = address.latitude
            location.location.longitude = address.longitude
            location.localityName = if (address.locality == null) "" else address.locality
            location.localityAdminAreaName = address.adminArea

//            Log.i("SeeAddress", "Location = ${location}}")
            mutableListAddressesLocation.add(location)
        }

        _listAddressesLocation.postValue(mutableListAddressesLocation.toList())
    }

    //Get information by coordinates location and return a custom object TrashLocation
    private fun getSingleTrashLocation(location: Location, context: Context): TrashLocation {
        geoCoder = Geocoder(context)

        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 4)[0]

        var trashLocation = TrashLocation()

        trashLocation.streetName = if (address.thoroughfare == null) "" else address.thoroughfare
        trashLocation.locality = if (address.locality == null) "" else address.locality

        val isFeatureNameNumber = Utils.isNumber(address.featureName)
        if (isFeatureNameNumber)
            trashLocation.feature = address.featureName

        trashLocation = addCommaTrashLocation(trashLocation, context)
        return trashLocation
    }

    /*
     * Get single addresslocation by location
     */
    private fun getSingleAddressLocation(location: Location, context: Context): AddressLocation {
        geoCoder = Geocoder(context)

        val address = geoCoder.getFromLocation(location.latitude, location.longitude, 1)[0]

        Log.i("SeeASingleAddress", "${address}")
        val addressLocation = AddressLocation()
        addressLocation.localityName = address.locality
        addressLocation.localityAdminAreaName = address.adminArea
        addressLocation.location = location

        return addressLocation
    }

    //I'm not using this yet
    //Get by information of addresses by location and return a list of custom object TrashLocation
    private fun getListAddressLocation(location: Location, context: Context): List<TrashLocation> {
        geoCoder = Geocoder(context)

        val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 4)

        val mutableListTrashLocation = mutableListOf<TrashLocation>()
        for (address in addresses) {
            Log.i(
                "SeeAddress",
                "Locality = ${address.locality}, PostalCode = ${address.postalCode} Sublocality= ${address.subLocality}"
            )
            val trashLocation = TrashLocation()
            trashLocation.streetName = address.thoroughfare
            trashLocation.locality = address.locality
            if (Utils.isNumber(address.featureName)) {
                trashLocation.feature = trashLocation.feature
            }

            mutableListTrashLocation.add(trashLocation)
        }

        return mutableListTrashLocation.toList()
    }

    /*
     * Format TrashLocation object
     * Add a comma if next word is available
     */
    private fun addCommaTrashLocation(
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
            trashLocation.streetName = context.resources.getString(R.string.trash_no_information)
            trashLocation.feature = ""
            trashLocation.locality = ""
        }
        return trashLocation
    }

    fun getTrashCluster(googleMap: GoogleMap, addressLocation: AddressLocation, context: Context) {
        val raw = getDataset(addressLocation)

        if (raw == -1) {
            _message.value = Event(context.getStringResources(R.string.dataset_not_found))
//            val message = context.resources.getString(R.string.dataset_not_found)
//            _message.value = Event(message)
            return
        }

        Log.i("SeeLayered", "Lets layer")

        val layer = GeoJsonLayer(googleMap, raw, context)

        val places = mutableListOf<Trash>()
        for (feature in layer.features) {

            Log.i("MapDeviceLocation", "Calculate")

            val latLng = feature.geometry.geometryObject as LatLng
            val locationPlace = Location("")
            locationPlace.latitude = latLng.latitude
            locationPlace.longitude = latLng.longitude

            val distance = addressLocation.location.distanceTo(locationPlace)
            Log.i("MapDeviceLocation", "${distance}")

//            listDistances.add(distance)
            if (distance < 100f) {
                //Get info street name where this trash is.
                val trashLocation = getSingleTrashLocation(locationPlace, context)

                val place = Trash(
                    latLng.latitude,
                    latLng.longitude,
                    "${trashLocation.streetName}${trashLocation.feature}${trashLocation.locality}",
                    "${distance}m"
                )

                places.add(place)
            }
        }

        _listTrash.value = places
    }


    private fun getDataset(addressLocation: AddressLocation): Int {
        var raw = -1
        Log.i(
            "SeeTrashCluster",
            "Getting trash cluster of locality = ${addressLocation.localityName} and localityAdmin = ${addressLocation.localityAdminAreaName}"
        )
        //Try to find the dataset in file Object LocalitiesDataset
        loopLocalityDataset@ for (localityDataset in LocalitesDataset.listLocalityDataset) {
            Log.i("SeeTrashCluster", "Enter loop")
            Log.i("SeeLocalityDataset", "Localiy name ${localityDataset.localityName}")
            //Some localities are with "" so they directly go to check the admin area
            if (addressLocation.localityName != "") {
                if (localityDataset.localityName != addressLocation.localityName) continue@loopLocalityDataset
            }

            Log.i("SeeLocalityDataset", "Pass first")
            /*
             * Split by comma because admin areas can have
             * different names example = "Canary Islands" == "Canarias"
             */
            val adminAreas = localityDataset.localityAdmin.split(',')
            loopAdminArea@ for (adminArea in adminAreas) {
                if (adminArea == addressLocation.localityAdminAreaName) {
                    Log.i("SeeLocalityDataset", "Admin area is equal!! localiydataset ${adminArea}")
                    raw = localityDataset.dataset
                    break@loopLocalityDataset
                }
                Log.i("SeeLocalityDataset", "Not equal :( ${adminArea}")
            }
        }

        return raw
    }

    fun setUpdateLocationByAddressLocation(addressLocation: AddressLocation) {
        listLocations.add(addressLocation)
        _addressLocation.postValue(addressLocation)
    }

    fun setUpdateLocationByLocation(location: Location, context: Context) {
        val addressLocation = getSingleAddressLocation(location, context)
        listLocations.add(addressLocation)
        _addressLocation.postValue(addressLocation)
    }

    fun getLastLocation() {
        if (listLocations.size > 1) {
            listLocations.removeLast()
            val newAddressLocation = listLocations.last()
            _addressLocation.value = newAddressLocation
        } else {
            _onBackPressed.value = true
        }
    }

    fun setGoogleMap(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    fun getGoogleMap(): GoogleMap {
        return googleMap
    }
}