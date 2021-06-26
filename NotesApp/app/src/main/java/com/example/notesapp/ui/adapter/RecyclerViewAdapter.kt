package com.example.notesapp.ui.adapter

import android.graphics.BitmapFactory
import android.os.Build
import android.text.Editable
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.data.entities.Note
import com.example.notesapp.databinding.ContainerNoteItemBinding
import com.example.notesapp.utils.DiffUtilNoteCallback
import com.example.notesapp.utils.getDateTimeString

class RecyclerViewAdapter:
    ListAdapter<Note, RecyclerViewAdapter.ViewHolder>(DiffUtilNoteCallback()) {
    private var recyclerViewCallback: RecyclerViewCallback? = null
    var tracker: SelectionTracker<Note>? = null

    inner class ViewHolder(private val binding: ContainerNoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(note: Note, isActivated: Boolean = false) {
            binding.apply {
                noteTitle.text = note.title
                noteContent.text = HtmlCompat.fromHtml(
                        note.noteText, HtmlCompat.FROM_HTML_MODE_COMPACT) as Editable?
                noteDatetime.text = getDateTimeString(note.dateTime)
                itemView.isActivated = isActivated

                if (note.labelColor.isNotEmpty())
                    noteLabelColor.setBackgroundColor(note.labelColor.toInt())

                noteImageContainer.visibility = if (note.imgPath != null) {
                    noteImage.setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
                    View.VISIBLE
                } else View.GONE
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Note> =
            object : ItemDetailsLookup.ItemDetails<Note>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): Note = getItem(adapterPosition)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ContainerNoteItemBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    recyclerViewCallback?.onRecyclerViewItemClicked(adapterPosition)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        tracker?.let {
            holder.bind(getItem(position), it.isSelected(getItem(position)))
        }
    }

    fun setOnCallbackListener(recyclerViewCallback: RecyclerViewCallback) {
        this.recyclerViewCallback = recyclerViewCallback
    }
}