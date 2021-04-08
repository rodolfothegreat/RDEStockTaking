package com.rde.android.rdestocktacking

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


internal class ReadOnlyTextWatcher(private val textEdit: EditText) : TextWatcher {
    private var originalText: String? = null
    private var mustUndo = true
    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        if (mustUndo) {
            originalText = charSequence?.toString()
        }
    }

    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun afterTextChanged(editable: Editable) {
        if (mustUndo) {
            mustUndo = false
            if(originalText != null)
                textEdit.setText(originalText)
        } else {
            mustUndo = true
        }
    }
}