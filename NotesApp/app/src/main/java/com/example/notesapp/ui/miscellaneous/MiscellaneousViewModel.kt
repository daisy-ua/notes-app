package com.example.notesapp.ui.miscellaneous

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MiscellaneousViewModel : ViewModel() {
    private val _labelColor = MutableLiveData<Int>()
    val labelColor: LiveData<Int?> get() = _labelColor

    fun onLabelColorSelected(color: Int) {
        _labelColor.value = color
    }

    private val _url = MutableLiveData<String>()
    val url: LiveData<String?> get() = _url

    fun onUrlSelected(text: String?) {
        _url.value = text
    }

    private val _image = MutableLiveData<Bitmap>()
    val image: LiveData<Bitmap?> get() = _image

    fun onImageSelected(bitmap: Bitmap?) {
        _image.value = bitmap
    }

    fun clear() {
        _image.value = null
        _url.value = null
        _labelColor.value = null
    }
}