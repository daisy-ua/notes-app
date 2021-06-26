package com.example.notesapp.ui.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.selection.SelectionTracker
import com.example.notesapp.R
import com.example.notesapp.data.entities.Note
import com.example.notesapp.ui.notes.NotesViewModel
import com.example.notesapp.utils.await
import com.example.notesapp.utils.showDeleteConfirmationDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

class ActionModeController(
    private val tracker: SelectionTracker<*>, private val activity: FragmentActivity
) : ActionMode.Callback {
    private var viewModel: NotesViewModel = ViewModelProvider(activity,
            ViewModelProvider.AndroidViewModelFactory(activity.application))
            .get(NotesViewModel::class.java)

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.selected_actions, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

    @ExperimentalCoroutinesApi
    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                val data = tracker.selection.toList().let {
                    it.filterIsInstance<Note>()
                        .apply { if (size != it.size) return false }
                }

                showDeleteConfirmationDialog(activity) {
                    viewModel.deleteItems(*data.toTypedArray())
                    mode?.finish()
                }
            }

            R.id.action_select_all -> {
                viewModel.viewModelScope.launch {
                    (tracker as SelectionTracker<Note>)
                            .setItemsSelected(viewModel.getNotes().await(), true)
                }
            }
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker.clearSelection()
    }
}