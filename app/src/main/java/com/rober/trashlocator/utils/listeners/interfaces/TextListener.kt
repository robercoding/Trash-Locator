package com.rober.trashlocator.utils.listeners.interfaces

interface TextListener {
    fun onUserStopTypingIsEmpty()
    fun onUserStopTyping(text: String)
}