package com.example.notesapp.utils

object Constants {
    const val REQUEST_CODE_STORAGE_PERMISSION = 1
    const val REQUEST_CODE_SELECT_IMAGE = 2
    const val TAG = "NOTES_APP"
}

object TextCodes {
    const val WHITESPACE = "&nbsp;"
    const val FONT_OPEN_TAG = "<font color='#[\\d\\D]{6}'>"

    fun getStyleTag(attr: Char) : String = "<$attr></$attr>"

    fun getClosingTag(closingTag: String) : String {
        if (Regex(("<font color='#[\\d\\D]{6}'>")).matches(closingTag))
            return "</font>"

        return when (closingTag) {
            "<b>" -> "</b>"
            "<i>" -> "</i>"
            "<u>" -> "</u>"
            else -> ""
        }
    }
}