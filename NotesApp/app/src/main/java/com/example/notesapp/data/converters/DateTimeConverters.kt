package com.example.notesapp.data.converters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
object DateTimeConverters {
    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    @TypeConverter
    @JvmStatic
    fun toOffsetDateTime(value: String) : LocalDateTime =
        formatter.parse(value, LocalDateTime::from)

    @TypeConverter
    @JvmStatic
    fun fromOffsetDateTime(dateTime: LocalDateTime) : String =
        dateTime.format(formatter)
}