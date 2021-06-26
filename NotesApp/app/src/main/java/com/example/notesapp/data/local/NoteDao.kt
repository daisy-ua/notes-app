package com.example.notesapp.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.notesapp.data.entities.Note

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY datetime(date_time) DESC")
    fun getNotes() : LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNotes(vararg note: Note)

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' " +
            "OR note_text LIKE '%' || :query || '%'")
    fun searchNotes(query: String) : LiveData<List<Note>>
}