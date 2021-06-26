package com.example.notesapp.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume

@ExperimentalCoroutinesApi
suspend fun <T> LiveData<T>.await(): T {
    return withContext(Dispatchers.Main.immediate) {
        suspendCancellableCoroutine { continuation ->
            val observer = object : Observer<T> {
                override fun onChanged(value: T) {
                    removeObserver(this)
                    continuation.resume(value)
                }
            }

            observeForever(observer)

            continuation.invokeOnCancellation {
                removeObserver(observer)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDateTimeString(datetime: LocalDateTime) : String {
    val currentDateTime = LocalDateTime.now()
    if (currentDateTime.year == datetime.year) {
        if (currentDateTime.dayOfYear == datetime.dayOfYear)
            return "Today " + datetime.format(DateTimeFormatter.ofPattern("hh:mm"))

        if (currentDateTime.minusDays(6).isBefore(datetime))
            return datetime.format(DateTimeFormatter.ofPattern("E"))

        return datetime.format(DateTimeFormatter.ofPattern("MMM dd"))
    }

    return datetime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
}