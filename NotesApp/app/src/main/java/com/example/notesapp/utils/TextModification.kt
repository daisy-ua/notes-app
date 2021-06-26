//package com.example.notesapp.utils
//
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.widget.EditText
//import androidx.core.text.HtmlCompat
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.Observer
//import java.lang.StringBuilder
//
//class TextModification(private val editText: EditText,
//                       textFormat: LiveData<Map<String, Any>>) : TextWatcher {
//
//    private var resultedText: String = ""
//    private var beforeLength: Int = 0
//    private var startChanged = 0
//    private var countChanged = 0
//
//
//    private var closingTag: String = ""
//
//    private var sb: StringBuilder? = null
//
//    val htmlText: String get() = "$resultedText$closingTag"
//
//
//    init {
//        textFormat.value?.apply {
//            (get("bold") as LiveData<*>).observeForever(styleTagObserver('b'))
//            (get("italic") as LiveData<*>).observeForever(styleTagObserver('i'))
//            (get("underline") as LiveData<*>).observeForever(styleTagObserver('u'))
//            (get("color") as LiveData<*>).observeForever(colorTagObserver())
//        }
//
//        sb = StringBuilder(resultedText)
//    }
//
//    fun setDefinedString(text: String) {
//        resultedText = text + resultedText
//    }
//
//    private fun colorTagObserver() = Observer<Any?> { value ->
//        if (value is Int && sb != null) {
//            val hexColor = String.format("#%06X", 0xFFFFFF and value)
//
//            val str = if (editText.selectionStart == 0
//                    || editText.selectionStart == editText.text.length - 1)
//                "<font color='$hexColor'></font>"
//            else {
//                val openTags = TextCodes.FONT_OPEN_TAG.toRegex().findAll(resultedText)
//                "</font><font color='$hexColor'></font>${openTags.last().value}"
//            }
//
//            resultedText = sb!!.append(resultedText)
//                    .insert(getHtmlCursorPosition(), str).toString()
//        }
//    }
//
//    private fun styleTagObserver(attr: Char) = Observer<Any?> { value ->
//        if (sb != null) {
//            if (value as Boolean) {
//                resultedText = StringBuilder(resultedText)
//                        .insert(getHtmlCursorPosition(), TextCodes.getStyleTag(attr)).toString()
//            }
////            else resultedText = sb!!.append(resultedText)
////                        .insert(getHtmlCursorPosition(attr), TextCodes.WHITESPACE).toString()
//            else {
//                val position = getHtmlCursorPosition(attr)
//                if (resultedText.substring(position, position + TextCodes.WHITESPACE.length - 1) != TextCodes.WHITESPACE)
//                    resultedText = StringBuilder(resultedText)
//                        .insert(getHtmlCursorPosition(attr), TextCodes.WHITESPACE).toString()
//            }
//        }
//
////        if (value as Boolean) {
////            closingTag = "</$attr>$closingTag"
////            openingTag = "<$attr>$openingTag"
////            resultedText += "<$attr>"
////        }
////        else {
////            if (closingTag != "") {
////                resultedText += "</font>$closingTag"
////                closingTag = closingTag.replace("</$attr>", "")
////                openingTag = openingTag.replace("<$attr>", "")
////                resultedText += openingTag
////            }
////        }
//    }
//
//    override fun beforeTextChanged(string: CharSequence?, start: Int, count: Int, after: Int) {
//        beforeLength = string?.length ?: 0
//    }
//
//    override fun afterTextChanged(string: Editable) {
//        editText.run {
//            removeTextChangedListener(this@TextModification)
//            text = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT) as Editable?
//            setSelection(startChanged + countChanged)
//            addTextChangedListener(this@TextModification)
//            Log.i("text", "'$htmlText'")
//        }
//    }
//
//    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//        val subString = s.toString().substring(start, start + count)
//
//        if (sb == null) return
//
//        startChanged = start
//        countChanged = count
//
//        val resultedCursorPosition = getHtmlCursorPosition()
//
//        Log.i("text", resultedText[resultedCursorPosition].toString())
//
//        resultedText = when {
//            s?.let { it.length < beforeLength} == true -> dropCharacter()
//
//            subString.last().isWhitespace() ->
//                StringBuilder(resultedText).insert(resultedCursorPosition, TextCodes.WHITESPACE).toString()
//
//            else -> StringBuilder(resultedText).insert(resultedCursorPosition, subString.takeLast(1)).toString()
//
//        }
//    }
//
//    private fun getHtmlCursorPosition(attr: Char? = null) : Int {
//        var currentCursorPosition = 0
//        val reversedCursorPosition = editText.text.length - editText.selectionStart
//        var counter = resultedText.length - 1
//
//        var closingTag = ""
//
//        while (counter > -1) {
//            var char = resultedText[counter]
//            if (char == '>') {
//                var tag = ""
//
//                while (char != '<') {
//                    counter--
//                    tag = "$char$tag"
//                    char = resultedText[counter]
//                }
//                tag = "$char$tag"
//
//                if (currentCursorPosition == reversedCursorPosition) {
//                    if (tag[2] == attr)
//                        return counter + tag.length
//
//                    if (tag[1] == '/') {
//                        closingTag = tag + closingTag
//                        counter--
//                        continue
//                    }
//
//                    val openingTag = TextCodes.getClosingTag(tag)
//                    if (closingTag != "" && closingTag.contains(openingTag))
//                        return counter + tag.length
//                }
//
//                counter--
//                continue
//            }
//
//            val str = resultedText.substring(counter - TextCodes.WHITESPACE.length + 1, counter + 1)
//            if (str == TextCodes.WHITESPACE) {
//                if (currentCursorPosition == reversedCursorPosition)
//                    return counter + 1
//
//                counter -= TextCodes.WHITESPACE.length
//                currentCursorPosition++
//                continue
//            }
//
//            if (currentCursorPosition == reversedCursorPosition)
//                return counter + 1
//            currentCursorPosition++
//            counter--
//        }
//
//        return 0
//    }
//
//    private fun dropCharacter() : String {
//        var cursorPosition = 0
//        val reversedPosition = editText.text.length - editText.selectionStart
//        var counter = resultedText.length - 1
//
//        mainLoop@while (counter > 0) {
//            var char = resultedText[counter]
//
//            if (char == '>') {
//                var tag = ""
//
//                while (char != '<') {
//                    counter--
//                    tag = "$char$tag"
//                    char = resultedText[counter]
//                }
//                tag = "$char$tag"
//
//                if (tag[1] == '/') {
//                    val str = resultedText.substring(counter - 3, counter)
//                    if (str == "<${tag.drop(2)}")
//                        resultedText = resultedText.removeRange(counter - str.length, counter + tag.length)
//                }
//
//                counter--
//                continue@mainLoop
//            }
//
//            val str = resultedText.substring(counter - TextCodes.WHITESPACE.length + 1, counter + 1)
//            if (str == TextCodes.WHITESPACE) {
//                if (cursorPosition == reversedPosition)
//                    return resultedText.removeRange(counter - TextCodes.WHITESPACE.length, counter + 1)
//
//                counter -= TextCodes.WHITESPACE.length
//                cursorPosition++
//                continue@mainLoop
//            }
//
//            if (cursorPosition == reversedPosition)
//                return resultedText.removeRange(counter, counter + 1)
//
//            cursorPosition++
//            counter--
//        }
//
//        return resultedText
//    }
//
//}
