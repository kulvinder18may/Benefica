package com.mysqldatabase.app.data.wrappers

import com.mysqldatabase.app.data.models.User
import io.reactivex.Observable

data class ObservableUser (
        override val id: String,
        val user: Observable<User>
): ItemViewModel {}