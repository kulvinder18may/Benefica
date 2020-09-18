package com.mysqldatabase.app.data.responses

import com.mysqldatabase.app.data.Status
import com.mysqldatabase.app.data.models.User

data class UserResponse (
        val status: Status,
        val user: User?,
        val error: String?
) {
    companion object {
        fun loading(): UserResponse = UserResponse(Status.LOADING, null, null)

        fun success(user: User): UserResponse
                = UserResponse(Status.SUCCESS, user, null)

        fun error(error: String): UserResponse = UserResponse(Status.ERROR, null, error)
    }
}