package com.panchayat.takoli.ui.callbacks

import android.view.View
import com.panchayat.takoli.data.models.PendingMeme

interface PendingMemesCallback {
    fun onPendingMemeClicked(view: View, meme: PendingMeme)
}