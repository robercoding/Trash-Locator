package com.rober.papelerasvalencia.utils

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