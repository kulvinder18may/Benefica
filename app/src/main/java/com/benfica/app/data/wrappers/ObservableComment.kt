package com.panchayat.takoli.data.wrappers

import com.panchayat.takoli.data.models.Comment
import io.reactivex.Observable

data class ObservableComment(
        val id: String,
        val comment: Observable<Comment>
) {}