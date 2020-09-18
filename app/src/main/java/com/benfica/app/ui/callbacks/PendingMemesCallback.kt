package com.mysqldatabase.app.ui.callbacks

import android.view.View
import com.mysqldatabase.app.data.models.PendingMeme

interface PendingMemesCallback {
    fun onPendingMemeClicked(view: View, meme: PendingMeme)
}