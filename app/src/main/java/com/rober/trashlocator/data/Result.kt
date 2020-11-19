package com.rober.trashlocator.data

sealed class Result<out T>{
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val exception: Exception, val data: T? = null) : Result<T>()
}