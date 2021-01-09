package com.panchayat.takoli.ui.callbacks

import android.view.View
import com.panchayat.takoli.data.models.Meme
import com.panchayat.takoli.data.models.User
import de.hdodenhof.circleimageview.CircleImageView

interface MemesCallback {
    fun onMemeClicked(view: View, meme: Meme)
    fun onProfileClicked(view: View, user: User)
}