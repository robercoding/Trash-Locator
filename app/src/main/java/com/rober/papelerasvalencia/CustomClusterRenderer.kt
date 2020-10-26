package com.rober.papelerasvalencia

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator
import com.google.maps.android.ui.SquareTextView
import com.rober.papelerasvalencia.models.Trash


class CustomClusterRenderer(
    val context: Context,
    val map: GoogleMap,
    val clusterManager: ClusterManager<Trash>
) : DefaultClusterRenderer<Trash>(context, map, clusterManager) {

    lateinit var iconGenerator :  IconGenerator
    lateinit var coloredCircleBackground : ShapeDrawable

    private val icons: SparseArray<BitmapDescriptor?> = SparseArray<BitmapDescriptor?>()


    override fun onBeforeClusterRendered(cluster: Cluster<Trash>, markerOptions: MarkerOptions) {
        iconGenerator = IconGenerator(context)
        coloredCircleBackground = ShapeDrawable(OvalShape())
        val density = context.resources.displayMetrics.density

        val squareTextView = SquareTextView(context)
        val layoutParams =  ViewGroup.LayoutParams(-2, -2)
        squareTextView.layoutParams =layoutParams
        squareTextView.id = com.google.maps.android.R.id.amu_text
        val twelveDpi = (12.0f * density).toInt()
        squareTextView.setPadding(twelveDpi, twelveDpi, twelveDpi, twelveDpi)
        squareTextView.text = cluster.size.toString()
        iconGenerator.setContentView(squareTextView)

        iconGenerator.setTextAppearance(com.google.maps.android.R.style.amu_ClusterIcon_TextAppearance)

        val outline = ShapeDrawable(OvalShape())
        outline.paint.setColor(ContextCompat.getColor(context, R.color.white))
        val background = LayerDrawable(arrayOf<Drawable>(outline, coloredCircleBackground))
        val strokeWidth = (density*3.0f).toInt()
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth)
        iconGenerator.setBackground(background)

        val bucket = getBucket(cluster)
        var descriptor = icons.get(bucket)
        if(descriptor == null){
//            coloredCircleBackground.paint.setColor(getColor(bucket))
            coloredCircleBackground.paint.setColor(ContextCompat.getColor(context, R.color.green))
            descriptor = BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon())
            icons.put(bucket, descriptor)
        }
        markerOptions.icon(descriptor)
    }

    override fun onBeforeClusterItemRendered(item: Trash, markerOptions: MarkerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions)
        markerOptions.icon(getBitmapDescriptorFromSVG())
        markerOptions.title(item.title)
    }

    private fun getBitmapDescriptorFromSVG(): BitmapDescriptor{
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