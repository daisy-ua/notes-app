package com.example.notesapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notesapp.data.converters.DateTimeConverters
import com.example.notesapp.data.entities.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun NoteDao() : NoteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if(INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    if(INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "notes.db"
                        ).build()
                    }
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance()  { INSTANCE = null }
    }
}