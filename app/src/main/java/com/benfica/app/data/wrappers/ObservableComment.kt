package com.mysqldatabase.app.data.wrappers

import com.mysqldatabase.app.data.models.Comment
import io.reactivex.Observable

data class ObservableComment(
        val id: String,
        val comment: Observable<Comment>
) {}