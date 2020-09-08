package com.benfica.app.data.wrappers

import com.benfica.app.data.models.Meme
import io.reactivex.Observable

data class ObservableMeme(
        override val id: String,
        val meme: Observable<Meme>
): ItemViewModel {
}