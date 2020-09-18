package com.mysqldatabase.app.data.wrappers

import com.mysqldatabase.app.data.models.Meme
import io.reactivex.Observable

data class ObservableMeme(
        override val id: String,
        val meme: Observable<Meme>
): ItemViewModel {
}