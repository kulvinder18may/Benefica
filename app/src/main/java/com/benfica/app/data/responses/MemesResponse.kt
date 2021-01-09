package com.panchayat.takoli.data.responses

import com.panchayat.takoli.data.Status
import com.panchayat.takoli.data.wrappers.ObservableMeme

data class MemesResponse(
        val status: Status,
        val data: ObservableMeme?,
        val error: String?
) {
    companion object {
        fun loading(): MemesResponse = MemesResponse(Status.LOADING, null, null)

        fun success(meme: ObservableMeme): MemesResponse
                = MemesResponse(Status.SUCCESS, meme, null)

        fun error(error: String): MemesResponse = MemesResponse(Status.ERROR, null, error)
    }
}