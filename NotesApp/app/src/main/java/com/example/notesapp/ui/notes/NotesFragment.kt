package com.example.notesapp.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notesapp.data.entities.Note
import com.example.notesapp.databinding.FragmentNotesBinding
import com.example.notesapp.ui.adapter.*
import com.example.notesapp.utils.DebounceQueryTextListener
import com.example.notesapp.utils.replaceFragment

class NotesFragment : Fragment(), RecyclerViewCallback {
    private var _binding: FragmentNotesBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel
    private lateinit var noteAdapter: RecyclerViewAdapter
    private var tracker: SelectionTracker<Note>? = null
    private var actionMode: ActionMode? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory(activity!!.application))
                .get(NotesViewModel::class.java)

        viewModel.getNotes().observe(viewLifecycleOwner, { notes ->
            noteAdapter.submitList(notes)
            binding.run {
                noteQuantity.text = notes.size.toString()
                if (notes.isEmpty()) {
                    notesList.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                } else {
                    notesList.visibility = View.VISIBLE
                    emptyView.visibility = View.GONE
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NotesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteAdapter = RecyclerViewAdapter().also {
            it.setOnCallbackListener(this)
        }

        binding.notesList.apply {
            adapter = noteAdapter
            setHasFixedSize(true)
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        noteAdapter.notifyDataSetChanged()

        setAdapterTracker()

        binding.addNoteButton.setOnClickListener {
            replaceFragment(NewNoteFragment.newInstance(), activity!!.supportFragmentManager, true)
        }

        binding.searchBar.setOnQueryTextListener(
                DebounceQueryTextListener(this.lifecycle) {
                    it?.let { query ->
                        if (it.isEmpty()) viewModel.resetSearchItems()
                        else viewModel.searchItems(query)
                    }
                }
        )
    }

    private fun setAdapterTracker() {
        tracker = SelectionTracker.Builder(
                "allNotesSelection",
                binding.notesList,
                NoteKeyProvider(noteAdapter),
                DetailsLookup(binding.notesList),
                StorageStrategy.createParcelableStorage(Note::class.java)
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build()

        noteAdapter.tracker = tracker

        tracker?.addObserver(
                object : SelectionTracker.SelectionObserver<Note>() {
                    override fun onSelectionChanged() {
                        super.onSelectionChanged()
                        tracker!!.let {
                            viewModel.selectItems(tracker!!.selection.toMutableList())
                            if (viewModel.selectedItems.isEmpty()) {
                                actionMode?.finish()
                                actionMode = null
                                binding.addNoteButton.visibility = View.VISIBLE
                            } else {
                                if (actionMode == null)
                                    actionMode = (activity as AppCompatActivity).startSupportActionMode(
                                            ActionModeController(tracker!!, activity!!))
                                actionMode?.title = "${viewModel.selectedItems.size}"
                                binding.addNoteButton.visibility = View.GONE
                            }
                        }
                    }
                }
        )
    }

    override fun onRecyclerViewItemClicked(position: Int) {
        viewModel.selectItem(noteAdapter.currentList[position])
        replaceFragment(NewNoteFragment.newInstance(), activity!!.supportFragmentManager, true)
    }
}