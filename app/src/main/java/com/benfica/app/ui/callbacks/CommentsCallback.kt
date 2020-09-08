package com.benfica.app.ui.callbacks

import android.view.View
import com.benfica.app.data.models.Comment

interface CommentsCallback {
    fun onCommentClicked(view: View, comment: Comment, longClick: Boolean)
}