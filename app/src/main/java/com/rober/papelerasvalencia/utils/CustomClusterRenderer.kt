package com.rober.papelerasvalencia.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.Log
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.google.maps.android.ui.SquareTextView
import com.rober.papelerasvalencia.R
import com.rober.papelerasvalencia.models.Trash


class CustomClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    val clusterManager: ClusterManager<Trash>
) : DefaultClusterRenderer<Trash>(context, map, clusterManager) {

    lateinit var iconGenerator: IconGenerator
    lateinit var coloredCircleBackground: ShapeDrawable

    private val icons: SparseArray<BitmapDescriptor?> = SparseArray<BitmapDescriptor?>()
    private val clusterSizes: SparseArray<Int?> = SparseArray<Int?>()

    override fun onBeforeClusterRendered(cluster: Cluster<Trash>, markerOptions: MarkerOptions) {
        val bucket = getBucket(cluster)
        val clusterSize = cluster.size

        val checkClusterSize = clusterSizes.get(bucket)

        if(checkClusterSize == null){
            makeIconDesign(cluster)
            val descriptor = icons.get(bucket)
            markerOptions.icon(descriptor)
            return
        }

        if (checkClusterSize == clusterSize) {
            val descriptor = icons.get(bucket)
            markerOptions.icon(descriptor)
            return
        }

        makeIconDesign(cluster)

        val descriptor = icons.get(bucket)
        markerOptions.icon(descriptor)
    }

    private fun makeIconDesign(cluster: Cluster<Trash>): Boolean {
        //Creates icon and a shapedrawable
        iconGenerator = IconGenerator(context)
        coloredCircleBackground = ShapeDrawable(OvalShape())
        val density = context.resources.displayMetrics.density

        //Define squareTextview and set it on IconGenerator
        val squareTextView = SquareTextView(context)
        val layoutParams = ViewGroup.LayoutParams(-2, -2)
        squareTextView.layoutParams = layoutParams
        squareTextView.id = com.google.maps.android.R.id.amu_text
        val twelveDpi = (12.0f * density).toInt()
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi)
        squareTextView.text = cluster.size.toString()
        iconGenerator.setContentView(squareTextView)

        iconGenerator.setTextAppearance(com.google.maps.android.R.style.amu_ClusterIcon_TextAppearance)

        //Add shape background and outline to IconGenerator
        val outline = ShapeDrawable(OvalShape())
        outline.paint.setColor(ContextCompat.getColor(context, R.color.blueLight2))
        val background = LayerDrawable(arrayOf<Drawable>(outline, coloredCircleBackground))
        val strokeWidth = (density * 3.0f).toInt()
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth)
        iconGenerator.setBackground(background)

        val bucket = getBucket(cluster)
        coloredCircleBackground.paint.setColor(getColor(bucket))
//        coloredCircleBackground.paint.setColor(ContextCompat.getColor(context, R.color.green))
        val descriptor = BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())

        icons.put(bucket, descriptor)
        clusterSizes.put(bucket, cluster.size)

        /*
        Added return boolean just in case any day I want to check if it's working!
        Actually I'm not using this check boolean when calling this function
         */

        if (descriptor != icons.get(bucket) || cluster.size != clusterSizes.get(bucket)) {
            return false
        }

        return true
    }

    override fun onClusterRendered(cluster: Cluster<Trash>, marker: Marker) {
        super.onClusterRendered(cluster, marker)

        val bucket = getBucket(cluster)
        val clusterSize = clusterSizes.get(bucket)

        if(clusterSize != null){

            return
        }

        makeIconDesign(cluster)
        val descriptor = icons.get(bucket)
        marker.setIcon(descriptor)
    }

    override fun onBeforeClusterItemRendered(item: Trash, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        markerOptions.icon(getBitmapDescriptorFromSVG())
        markerOptions.title(item.title)
    }

    private fun getBitmapDescriptorFromSVG(): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, R.drawable.ic_garbage)
        val bitmap = Bitmap.createBitmap(
            vectorDrawable!!.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}