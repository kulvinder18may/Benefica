package com.mysqldatabase.app.ui.callbacks

import android.view.View
import com.mysqldatabase.app.data.models.Meme
import com.mysqldatabase.app.data.models.User
import de.hdodenhof.circleimageview.CircleImageView

interface MemesCallback {
    fun onMemeClicked(view: View, meme: Meme)
    fun onProfileClicked(view: View, user: User)
}