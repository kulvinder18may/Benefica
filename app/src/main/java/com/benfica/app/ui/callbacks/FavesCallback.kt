package com.benfica.app.ui.callbacks

import com.benfica.app.data.models.Fave
import com.makeramen.roundedimageview.RoundedImageView

interface FavesCallback {
    fun onFaveClick(view: RoundedImageView, meme: Fave, longClick: Boolean)
}