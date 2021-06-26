package com.example.notesapp.ui.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.entities.Note

class DetailsLookup(private val recyclerView: RecyclerView) :
        ItemDetailsLookup<Note>() {
    override fun getItemDetails(e: MotionEvent): ItemDetails<Note>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as RecyclerViewAdapter.ViewHolder)
                    .getItemDetails()
        }
        return null
    }
}

class NoteKeyProvider(private val adapter: RecyclerViewAdapter) :
        ItemKeyProvider<Note>(SCOPE_CACHED) {
    override fun getKey(position: Int): Note = adapter.currentList[position]
    override fun getPosition(key: Note): Int = adapter.currentList.indexOf(key)
}
