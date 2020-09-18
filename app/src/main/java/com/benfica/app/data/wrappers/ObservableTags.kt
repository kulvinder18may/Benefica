package com.mysqldatabase.app.data.wrappers

import com.mysqldatabase.app.data.models.Meme
import io.reactivex.Observable

data class ObservableTags(
        override val id: String,
        val meme: Observable<Meme>
): ItemViewModel {
}