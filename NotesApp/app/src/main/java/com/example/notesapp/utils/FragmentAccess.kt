package com.example.notesapp.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Patterns
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import com.example.notesapp.R
import com.example.notesapp.databinding.LayoutAddUrlBinding
import com.example.notesapp.databinding.LayoutDeleteNoteBinding
import com.example.notesapp.utils.Constants.TAG
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun replaceFragment (fragment: Fragment, supportFragmentManager: FragmentManager, isTransition: Boolean = false) {
    val fragmentTransition = supportFragmentManager.beginTransaction()

    val backStateName = fragment.javaClass.simpleName
    val fragmentPopped = supportFragmentManager.popBackStackImmediate(backStateName, 0)

    if (isTransition) {
        fragmentTransition.setCustomAnimations(
                android.R.anim.slide_in_left,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.slide_out_right
        )
    }

    if (!fragmentPopped) {
        fragmentTransition.replace(R.id.frame_layout, fragment, TAG).addToBackStack(null).commit()
    }
}

fun EditText.showSoftKeyboard() {
    (this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun hideSoftKeyboard(activity: FragmentActivity) {
    (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)

}

fun showAddURLDialog(context: Context, onAddCallback: (String) -> Unit) {
    val builder = AlertDialog.Builder(context)
    val viewBinding = LayoutAddUrlBinding.inflate(LayoutInflater.from(context))
    builder.setView(viewBinding.root)

    val dialog = builder.create().also {
        it.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    viewBinding.run {
        inputUrl.requestFocus()

        addOption.setOnClickListener{
            val text = inputUrl.text.toString()

            if (text.trim().isEmpty()) {
                Toast.makeText(context, "URL is empty !", Toast.LENGTH_SHORT).show()
            } else if(!Patterns.WEB_URL.matcher(text).matches()) {
                Toast.makeText(context, "Enter valid URL", Toast.LENGTH_SHORT).show()
            } else {
                onAddCallback(text)
                dialog.dismiss()
            }
        }

        cancelOption.setOnClickListener {
            dialog.dismiss()
        }
    }
    dialog.show()
}

fun showDeleteConfirmationDialog(context: Context, onDeleteCallback: () -> Unit) {
    val builder = AlertDialog.Builder(context)
    val viewBinding = LayoutDeleteNoteBinding.inflate(LayoutInflater.from(context))
    builder.setView(viewBinding.root)

    val dialogDeleteNote = builder.create().also {
        it.window?.setBackgroundDrawable(ColorDrawable(0))
    }

    viewBinding.run {
        deleteOption.setOnClickListener{
            onDeleteCallback()
            dialogDeleteNote!!.dismiss()
        }

        cancelOption.setOnClickListener {
            dialogDeleteNote!!.dismiss()
        }
    }
    dialogDeleteNote?.show()
}

class DebounceQueryTextListener(
        lifecycle: Lifecycle,
        private val onDebounceQueryTextChange: (String?) -> Unit
) : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
    private var debouncePeriod: Long = 300

    private val coroutineScope = lifecycle.coroutineScope

    private var searchJob: Job? = null

    override fun onQueryTextSubmit(query: String?): Boolean = false

    override fun onQueryTextChange(query: String?): Boolean {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            query?.let {
                delay(debouncePeriod)
                onDebounceQueryTextChange(query)
            }
        }
        return false
    }
}