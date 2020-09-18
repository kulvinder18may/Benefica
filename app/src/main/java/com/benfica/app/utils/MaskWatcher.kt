package com.benfica.app.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by AblySoft Pvt Ltd. on 18/9/20.
 */


class MaskWatcher(mask: String) : TextWatcher {
    private var isRunning = false
    private var isDeleting = false
    private val mask: String
    override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
        isDeleting = count > after
    }

    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(editable: Editable) {
        if (isRunning || isDeleting) {
            return
        }
        isRunning = true
        val editableLength = editable.length
        if (editableLength < mask.length) {
            if (mask[editableLength] != '#') {
                editable.append(mask[editableLength])
            } else if (mask[editableLength - 1] != '#') {
                editable.insert(editableLength - 1, mask, editableLength - 1, editableLength)
            }
        }
        isRunning = false
    }

    companion object {
        fun buildCpf(): MaskWatcher {
            return MaskWatcher("##-##-##-##-##-##-##")
        }
    }

    init {
        this.mask = mask
    }
}