package com.rober.trashlocator

import android.os.IBinder
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
import android.view.WindowManager.LayoutParams.TYPE_TOAST
import androidx.test.espresso.Root
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class ToastMatcher : TypeSafeMatcher<Root?>() {

    override fun describeTo(description: Description?) {
        description?.appendText("is toast")
    }

    override fun matchesSafely(item: Root?): Boolean {
        val type: Int? = item?.windowLayoutParams?.get()?.type
        if (type == TYPE_TOAST || type == TYPE_APPLICATION_OVERLAY) {
            val windowToken: IBinder = item.decorView.windowToken
            val appToken: IBinder = item.decorView.applicationWindowToken
            if (windowToken === appToken) { // means this window isn't contained by any other windows.
                return true
            }
        }
        return false
    }
}