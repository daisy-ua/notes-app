package com.example.notesapp.ui.notes

import android.app.Application
import androidx.lifecycle.*
import com.example.notesapp.data.entities.Note
import com.example.notesapp.data.local.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class NotesViewModel (application: Application) : AndroidViewModel(application) {
    private var repository: AppRepository = AppRepository(application)

    private val _notes: LiveData<List<Note>>

    private val mutableSelectedItem = MutableLiveData<Note>()
    val selectedItem: LiveData<Note> get() = mutableSelectedItem

    private var mutableSelectedItems: MutableList<Note> = mutableListOf()
    val selectedItems: List<Note> get() = mutableSelectedItems

    private val _query = MutableLiveData<String>()

    var selectedImagePath: String? = null

    init {
        _notes = repository.getNotes()
        _query.postValue("")
    }

    fun selectItem(note: Note?) {
        mutableSelectedItem.postValue(note)
    }

    fun getNotes() = Transformations.switchMap(_query) {
        if (it == "") _notes else repository.searchNotes(it)
    }

    fun insertItem(title: String, subtitle: String, datetime: LocalDateTime,
                   content: String, color: String, url: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertNote(Note(
            selectedItem.value?.id ?: 0,
            title, subtitle, datetime, content, color,
                selectedItem.value?.imgPath ?: selectedImagePath, url
        ))
    }

    fun deleteItems(vararg notes : Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotes(*notes)
    }

    fun searchItems(query: String) = _query.postValue(query)

    fun resetSearchItems() = _query.postValue("")

    fun selectItems(notes: MutableList<Note>) {
        mutableSelectedItems = notes
    }
}