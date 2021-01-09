package com.panchayat.takoli.ui.callbacks

import android.view.View
import com.panchayat.takoli.data.models.Meme
import com.panchayat.takoli.data.models.User

interface ProfileMemesCallback : MemesCallback {
    fun onMemeLongClicked(meme: Meme)

    override fun onMemeClicked(view: View, meme: Meme)

    override fun onProfileClicked(view: View, user: User)
}