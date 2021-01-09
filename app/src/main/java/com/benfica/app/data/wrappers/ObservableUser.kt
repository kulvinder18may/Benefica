package com.panchayat.takoli.data.wrappers

import com.panchayat.takoli.data.models.User
import io.reactivex.Observable

data class ObservableUser (
        override val id: String,
        val user: Observable<User>
): ItemViewModel {}