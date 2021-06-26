package com.example.notesapp.ui.notes

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.*
import android.os.Build
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.example.notesapp.databinding.FragmentNewNoteBinding
import com.example.notesapp.ui.miscellaneous.Miscellaneous
import com.example.notesapp.ui.miscellaneous.MiscellaneousViewModel
import com.example.notesapp.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import java.util.*
import com.example.notesapp.utils.*
import java.time.LocalDateTime

class NewNoteFragment : Fragment() {
    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotesViewModel
    private lateinit var fragmentViewModel: MiscellaneousViewModel

    private val miscellaneous: Miscellaneous by lazy {
        Miscellaneous((view!!.parent as ViewGroup), this)
    }

    fun getBottomSheetDialogVisibleArea(outRect: Rect) =
            miscellaneous.getVisibleArea(outRect)

    fun setMiscellaneousState(state: Int) = miscellaneous.setMiscellaneousState(state)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory(activity!!.application))
                .get(NotesViewModel::class.java)

        fragmentViewModel = ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory(activity!!.application))
                .get(MiscellaneousViewModel::class.java)

        fragmentViewModel.labelColor.observe(this, Observer(::onLabelColorChanged))
        fragmentViewModel.url.observe(this, Observer(::onUrlChanged))
        fragmentViewModel.image.observe(this, Observer(::onImageChanged))

        return binding.root
    }

    private fun onLabelColorChanged(color: Int?) =
            color?.let {
                binding.noteLabelColor.setBackgroundColor(it)
            }

    private fun onUrlChanged(text: String?) =
            text?.let {
                binding.run {
                    noteUrlText.text = it
                    layoutUrl.visibility = View.VISIBLE
                }
            }

    private fun onImageChanged(bitmap: Bitmap?) {
        bitmap?.let {
            binding.noteImage.run {
                setImageBitmap(it)
                visibility = View.VISIBLE
            }
            binding.removeImage.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        miscellaneous.destroyView()
        viewModel.selectItem(null)
        fragmentViewModel.clear()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                NewNoteFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.saveBtn.setOnClickListener {

            if (binding.noteContent.length() == 0 && binding.noteTitle.length() == 0)
                return@setOnClickListener

            if (binding.noteTitle.length() == 0) {
                binding.noteTitle.setText(getNoteTitleIfEmpty(binding.noteContent))
            }

            viewModel.insertItem(
                    binding.noteTitle.text.toString(),
                    binding.noteSubtitle.text.toString(),
                    LocalDateTime.now(),
                    binding.noteContent.text.toString(),
                    binding.noteLabelColor.background?.let {
                        (it as ColorDrawable).color.toString()
                    } ?: "",
                    binding.noteUrlText.text.toString()
            )

            viewModel.selectItem(null)
            replaceFragment(NotesFragment.newInstance(), activity!!.supportFragmentManager, true)
        }

        binding.backBtn.setOnClickListener {
            viewModel.selectItem(null)
            replaceFragment(NotesFragment.newInstance(), activity!!.supportFragmentManager, true)
        }

        binding.run {
            removeUrl.setOnClickListener {
                noteUrlText.text = null
                layoutUrl.visibility = View.GONE
                fragmentViewModel.onUrlSelected(null)
                binding.layoutUrl.visibility = View.GONE
            }
        }

        binding.run {
            removeImage.setOnClickListener {
                noteImage.setImageBitmap(null)
                noteImage.visibility = View.GONE
                removeImage.visibility = View.GONE
                viewModel.selectedImagePath = null
                fragmentViewModel.onImageSelected(null)
                binding.noteImage.visibility = View.GONE
            }
        }

        lifecycleScope.launchWhenStarted {
            miscellaneous.initListeners()
            if (viewModel.selectedItem.value != null ) setNoteFields()
            binding.noteContent.requestFocus()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNoteFields() {
        viewModel.selectedItem.value!!.let { note ->
            val htmlContent = HtmlCompat.fromHtml(note.noteText, HtmlCompat.FROM_HTML_MODE_COMPACT) as Editable?
            binding.noteContent.setText(note.noteText)

            binding.run {
                noteTitle.setText(note.title)
                noteDatetime.text = getDateTimeString(note.dateTime)
                noteContent.text = htmlContent
                noteSubtitle.setText(note.subTitle)
                if (note.labelColor.isNotEmpty())
                    noteLabelColor.setBackgroundColor(note.labelColor.toInt())

                noteImage.run {
                    visibility = if (note.imgPath != null) {
                        setImageBitmap(BitmapFactory.decodeFile(note.imgPath))
                        removeImage.visibility = View.VISIBLE
                        View.VISIBLE
                    } else View.GONE
                }

                layoutUrl.visibility = if (!note.webLink.isNullOrEmpty()) {
                    noteUrlText.text = note.webLink
                    removeUrl.visibility = View.VISIBLE
                    View.VISIBLE
                } else View.GONE
            }
        }

    }

    private fun getNoteTitleIfEmpty(editText: EditText): String {
        val startPoint = editText.layout.getLineStart(0)
        val endPoint = editText.layout.getLineEnd(0)
        return editText.text.substring(startPoint, endPoint)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                miscellaneous.selectImage()
            } else {
                Toast.makeText(activity, "Permission Denied !", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        miscellaneous.onActivityResultImage(requestCode, resultCode,data)
    }
}