package com.benfica.app.ui.callbacks

import android.view.View
import com.benfica.app.data.models.Meme
import com.benfica.app.data.models.User

interface ProfileMemesCallback : MemesCallback {
    fun onMemeLongClicked(meme: Meme)

    override fun onMemeClicked(view: View, meme: Meme)

    override fun onProfileClicked(view: View, user: User)
}