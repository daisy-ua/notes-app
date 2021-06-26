package com.example.notesapp.data.entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
@Entity(tableName = "notes")
data class Note (

    @PrimaryKey(autoGenerate = true)
    var id: Long,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "sub_title")
    var subTitle: String,

    @ColumnInfo(name = "date_time")
    var dateTime: LocalDateTime,

    @ColumnInfo(name = "note_text")
    var noteText: String,

    @ColumnInfo(name = "label_color")
    var labelColor: String,

    @ColumnInfo(name = "img_path")
    var imgPath: String?,

    @ColumnInfo(name = "web_link")
    var webLink: String?,

) : Parcelable