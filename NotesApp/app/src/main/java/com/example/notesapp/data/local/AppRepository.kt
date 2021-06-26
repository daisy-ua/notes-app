package com.example.notesapp.data.local

import android.app.Application
import com.example.notesapp.data.entities.Note

class AppRepository(application: Application) {
    private var noteDao: NoteDao

    init {
        val db = AppDatabase.getInstance(application)
        noteDao = db.NoteDao()
    }

    fun getNotes() = noteDao.getNotes()

    suspend fun insertNote(note: Note) = noteDao.insertNote(note)

    suspend fun deleteNotes(vararg notes: Note) = noteDao.deleteNotes(*notes)

    fun searchNotes(query: String) = noteDao.searchNotes(query)
}