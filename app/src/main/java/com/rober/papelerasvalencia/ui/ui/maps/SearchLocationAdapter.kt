package com.rober.papelerasvalencia.ui.ui.maps

import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.models.AddressLocation
import com.rober.papelerasvalencia.utils.listeners.interfaces.RecyclerAddressLocationClickListener

class SearchLocationAdapter(
    private val listAddressLocation: List<AddressLocation>,
    private val recyclerAddressLocationClickListener: RecyclerAddressLocationClickListener
) : RecyclerView.Adapter<SearchLocationAdapter.SearchLocationViewHolder>() {

    class SearchLocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var containerRowAddress: ConstraintLayout? = null
        var textRowAddress: TextView? = null

        init {
            containerRowAddress = itemView.findViewById(R.id.containerFragment)
            textRowAddress = itemView.findViewById(R.id.rowAddress)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchLocationViewHolder {
        return SearchLocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row_addresslocation, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchLocationViewHolder, position: Int) {
        bind(holder, position)
    }

    private fun bind(holder: SearchLocationViewHolder, position: Int) {
        holder.textRowAddress?.text = listAddressLocation[position].streetName

        holder.textRowAddress?.setOnClickListener {
            Log.i("SeeClick", "Clicked on adapter text")
            val location = Location("")
            location.latitude = listAddressLocation[position].latitude
            location.longitude = listAddressLocation[position].longitude
            recyclerAddressLocationClickListener.onAddressLocationClickListener(location)
        }
    }

    override fun getItemCount(): Int {
        return listAddressLocation.size
    }
}