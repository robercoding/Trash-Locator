package com.rober.trashlocator.utils.listeners

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.rober.trashlocator.utils.EspressoIdlingResource
import com.rober.trashlocator.utils.listeners.interfaces.TextListener
import java.util.*

class TextWatcherListener(val textListener: TextListener) : TextWatcher {

    private var timer: Timer? = null
    private var isSettingText =
        false //Flag that checks if text being set programatically so it don't trigger textwatcher listener

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        timer?.cancel()
    }

    override fun afterTextChanged(s: Editable?) {
        EspressoIdlingResource.decrement()
        EspressoIdlingResource.increment()
        if (isSettingText) {
            EspressoIdlingResource.decrement()
            return
        }
        timer = Timer()

        timer?.schedule(object : TimerTask() {
            override fun run() {
                val text = s?.trim().toString()

                if (text.isEmpty()) {
                    textListener.onUserStopTypingIsEmpty()
                    return
                }
                textListener.onUserStopTyping(text)
            }

        }, 600)
    }

    fun isSettingText(settingText: Boolean) {
        isSettingText = settingText
    }
}