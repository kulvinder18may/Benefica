package com.mysqldatabase.app.ui.callbacks

import android.view.View
import com.mysqldatabase.app.data.models.Fave
import com.makeramen.roundedimageview.RoundedImageView

interface FavesCallback {
    fun onFaveClick(view: RoundedImageView, meme: Fave, longClick: Boolean)
    fun onCommentClicked(view: View, meme: Fave)
}