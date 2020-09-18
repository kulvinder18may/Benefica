package com.mysqldatabase.app.ui.callbacks

import android.view.View
import com.mysqldatabase.app.data.models.Comment

interface CommentsCallback {
    fun onCommentClicked(view: View, comment: Comment, longClick: Boolean)
}