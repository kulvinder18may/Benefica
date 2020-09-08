package com.benfica.app.data.models

import java.io.Serializable

data class Fave(
        var id: String? = null,
        var imageUrl: String? = null,
        var caption: String? = null,
        var memePoster: String? = null,
        var memePosterAvatar: String? = null,
        var likesCount: Int = 0,
        var commentsCount: Int = 0,
        var time: Long? = null,
        var hashTag: String? = null,
        var city: String? = null,
        var likes: MutableMap<String, Boolean> = mutableMapOf(),
        var faves: MutableMap<String, Boolean> = mutableMapOf()
): Serializable {
    fun equals(fave: Fave): Boolean = this.id == fave.id
}