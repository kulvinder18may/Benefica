package com.panchayat.takoli.data.wrappers

import com.panchayat.takoli.data.models.Meme
import io.reactivex.Observable

data class ObservableMeme(
        override val id: String,
        val meme: Observable<Meme>
): ItemViewModel {
}