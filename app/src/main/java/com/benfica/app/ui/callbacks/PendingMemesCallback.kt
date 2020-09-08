package com.benfica.app.ui.callbacks

import android.view.View
import com.benfica.app.data.models.PendingMeme

interface PendingMemesCallback {
    fun onPendingMemeClicked(view: View, meme: PendingMeme)
}