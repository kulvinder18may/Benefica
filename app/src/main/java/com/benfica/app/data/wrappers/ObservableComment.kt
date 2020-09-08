package com.benfica.app.data.wrappers

import com.benfica.app.data.models.Comment
import io.reactivex.Observable

data class ObservableComment(
        val id: String,
        val comment: Observable<Comment>
) {}