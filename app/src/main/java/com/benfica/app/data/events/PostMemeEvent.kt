package com.benfica.app.data.events

data class PostMemeEvent(
        val type: TYPE
) {
    enum class TYPE {
        NEW_POST,
        FAVORITE
    }
}