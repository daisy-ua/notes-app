package com.example.notesapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.notesapp.data.entities.Note

class DiffUtilNoteCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}