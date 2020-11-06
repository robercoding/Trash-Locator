package com.rober.papelerasvalencia.utils

import android.content.Context
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

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
