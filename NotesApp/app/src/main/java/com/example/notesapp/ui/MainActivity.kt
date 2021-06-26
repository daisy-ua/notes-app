package com.example.notesapp.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.notesapp.R.layout.activity_main
import com.example.notesapp.ui.notes.NewNoteFragment
import com.example.notesapp.ui.notes.NotesFragment
import com.example.notesapp.utils.Constants.TAG
import com.example.notesapp.utils.replaceFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        replaceFragment(NotesFragment.newInstance(), supportFragmentManager)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val fr = supportFragmentManager.findFragmentByTag(TAG)
        if (fr != null && fr is NewNoteFragment && fr.isVisible) {
            if (ev?.action == MotionEvent.ACTION_DOWN) {
                val outRect = Rect()
                fr.getBottomSheetDialogVisibleArea(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt()))
                    fr.setMiscellaneousState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }

        return super.dispatchTouchEvent(ev)
    }
}