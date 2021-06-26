package com.example.notesapp.ui.miscellaneous

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.notesapp.R
import com.example.notesapp.databinding.LayoutBottomSheetBinding
import com.example.notesapp.databinding.LayoutColorPaletteBinding
import com.example.notesapp.ui.notes.NotesFragment
import com.example.notesapp.ui.notes.NotesViewModel
import com.example.notesapp.utils.*
import com.example.notesapp.utils.Constants.REQUEST_CODE_STORAGE_PERMISSION
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.lang.Exception

class Miscellaneous(private val parent: ViewGroup, private val fr: Fragment) {
    private val fragmentViewModel: MiscellaneousViewModel
    private val viewModel: NotesViewModel

    private var _binding: LayoutBottomSheetBinding? = null
    private val binding get() = _binding!!

    private val bottomSheetBehavior: BottomSheetBehavior<CardView> by lazy {
        BottomSheetBehavior.from(binding.layoutMiscellaneous)
    }

    init {
        _binding = LayoutBottomSheetBinding.inflate(LayoutInflater.from(parent.context), parent, true)

        viewModel = ViewModelProvider(parent.context as FragmentActivity,
                ViewModelProvider.AndroidViewModelFactory((parent.context as Activity).application))
                .get(NotesViewModel::class.java)

        if (viewModel.selectedItem.value != null) binding.layoutRemoveNote.visibility = View.VISIBLE

        fragmentViewModel = ViewModelProvider(parent.context as FragmentActivity,
                ViewModelProvider.AndroidViewModelFactory((parent.context as Activity).application))
                .get(MiscellaneousViewModel::class.java)
    }

    fun destroyView() {
        parent.removeView(binding.root)
        fragmentViewModel.clear()
        _binding = null
    }

    fun initListeners() {
        binding.run {
            miscellaneousPersistent.setOnClickListener(actionMoreListener)

            setColorPaletteListener(binding.labelColorPalette) { selectedColor ->
                fragmentViewModel.onLabelColorSelected(selectedColor)
            }

            layoutAddImage.setOnClickListener(addImageListener)

            layoutAddUrl.setOnClickListener(addURLListener)

            layoutRemoveNote.setOnClickListener(removeNoteListener)
        }
    }

    fun setMiscellaneousState(state: Int) = state.also { bottomSheetBehavior.state = it }

    fun onActivityResultImage(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK)
            if (data != null) {
                val selectedImageURI = data.data
                if (selectedImageURI != null) {
                    try {
                        val inputStream = fr.context!!.contentResolver.openInputStream(selectedImageURI)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        fragmentViewModel.onImageSelected(bitmap)
                        viewModel.selectedImagePath = getPathFromURI(selectedImageURI)
                    } catch (e: Exception) {
                        Toast.makeText(fr.requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(parent.context.packageManager) != null) {
            fr.startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_IMAGE)
        }
    }

    fun getVisibleArea(outRect: Rect) =
            binding.layoutMiscellaneous.getGlobalVisibleRect(outRect)

    @SuppressLint("Recycle")
    private fun getPathFromURI(uri: Uri) : String? {
        var filePath: String? = null
        val cursor = fr.context!!.contentResolver.query(uri, null, null, null, null)
        if (cursor == null) {
            filePath = uri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
    }

    private fun setColorPaletteListener(labelColorPalette: LayoutColorPaletteBinding,
                                        callback: (Int) -> Unit
    ) {
        labelColorPalette.root.apply {
        for (count in 0 until childCount) {
                val frameChild = getChildAt(count) as FrameLayout
                for (frameCounts in 0 until frameChild.childCount) {
                    val child = frameChild.getChildAt(frameCounts)
                    if (child is View)
                        child.setOnClickListener(colorSelectedListener(
                                labelColorPalette,
                                callback
                        ))
                }
            }
        }
    }

    private val actionMoreListener = View.OnClickListener {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        else
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private val colorSelectedListener: ((labelColorPalette: LayoutColorPaletteBinding,
                                         callback: (Int) -> Unit) -> View.OnClickListener
    ) = { labelColorPalette: LayoutColorPaletteBinding, callback: (Int) -> Unit ->
        View.OnClickListener {
            val background = it.background as LayerDrawable
            val gradientDrawable = background.findDrawableByLayerId(R.id.main_color) as GradientDrawable

            var selectedColor = 0

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N)
                selectedColor = gradientDrawable.color!!.defaultColor

            callback(selectedColor)

            labelColorPalette.run {
                imageColor1.visibility = View.GONE
                imageColor2.visibility = View.GONE
                imageColor3.visibility = View.GONE
                imageColor4.visibility = View.GONE
                imageColor5.visibility = View.GONE
                imageColor6.visibility = View.GONE
                imageColor7.visibility = View.GONE

                when(it.id) {
                    R.id.view_color_1 -> imageColor1.visibility = View.VISIBLE
                    R.id.view_color_2 -> imageColor2.visibility = View.VISIBLE
                    R.id.view_color_3 -> imageColor3.visibility = View.VISIBLE
                    R.id.view_color_4 -> imageColor4.visibility = View.VISIBLE
                    R.id.view_color_5 -> imageColor5.visibility = View.VISIBLE
                    R.id.view_color_6 -> imageColor6.visibility = View.VISIBLE
                    R.id.view_color_7 -> imageColor7.visibility = View.VISIBLE
                }
            }
        }
    }

    private val addImageListener = View.OnClickListener {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        if (ContextCompat.checkSelfPermission(
                        parent.context.applicationContext,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    parent.context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_STORAGE_PERMISSION
            )
        } else {
            selectImage()
        }
    }

    private val addURLListener = View.OnClickListener {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        showAddURLDialog(parent.context) { text -> fragmentViewModel.onUrlSelected(text) }
    }

    private val removeNoteListener = View.OnClickListener {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        showDeleteConfirmationDialog(parent.context) {
            viewModel.deleteItems(viewModel.selectedItem.value!!)
            replaceFragment(NotesFragment.newInstance(), (parent.context as FragmentActivity).supportFragmentManager, true)
        }
    }
}
