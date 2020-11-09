package com.rober.trashlocator.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import java.util.*

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun DrawerLayout.closeDrawer() {
    this.closeDrawer(GravityCompat.START)
}

fun DrawerLayout.openDrawer() {
    this.openDrawer(GravityCompat.START)
}

fun Context.getStringResources(string: Int): String {
    return this.resources.getString(string)
}

@Suppress("DEPRECATION")
fun Configuration.getLocaleCompat(): Locale {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) locales.get(0) else locale
}
