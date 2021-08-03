package com.example.messenger

sealed class DataState<out R> {

    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val exception: Exception) : DataState<Nothing>()

    object Loading : DataState<Nothing>()
    object Canceled : DataState<Nothing>()

    object Empty : DataState<Nothing>()
}