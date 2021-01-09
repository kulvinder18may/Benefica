package com.panchayat.takoli.ui.callbacks

import android.view.View
import com.panchayat.takoli.data.models.Comment

interface CommentsCallback {
    fun onCommentClicked(view: View, comment: Comment, longClick: Boolean)
}