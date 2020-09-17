package com.benfica.app.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.benfica.app.data.Status
import com.benfica.app.data.repositories.MemesRepository
import com.benfica.app.data.wrappers.ItemViewModel
import com.benfica.app.data.wrappers.ObservableUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MemesDataSource constructor(private val repository: MemesRepository,
                                  private val scope: CoroutineScope,
                                  private val user: ObservableUser ?= null,
                                  private val searchKeyword: String ?= null,
                                  private val isVideoOnly: Boolean ?= false,
                                  private val status: (Status) -> Unit): ItemKeyedDataSource<String, ItemViewModel>() {

    class Factory(private val repository: MemesRepository,
                  private val scope: CoroutineScope,
                  private val user: ObservableUser? = null,
                  private val searchKeyword: String ?= null,
                  private val isVideoOnly: Boolean ?= false,
                  private val status: (Status) -> Unit): DataSource.Factory<String, ItemViewModel>() {

        override fun create(): DataSource<String, ItemViewModel> {
            return MemesDataSource(repository, scope, user,searchKeyword,isVideoOnly, status)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<ItemViewModel>) {
        Timber.e("Loading initial ${if (user == null) "Profile" else "Home"}...")
        status(Status.LOADING)

        scope.launch {




             if (user != null&& !isVideoOnly!!) {

                val memes = repository.fetchMemesByUser(user.id)
                memes.add(0, user)

                if (memes.size == 1) status(Status.ERROR) else status (Status.SUCCESS)
                callback.onResult(memes)

            }
           else if (isVideoOnly!!) {

                 val memes = repository.getVideoMemeOnly(searchKeyword!! )

                 Timber.e("loaded: ${memes.size} memes")

                 if (memes.isEmpty()) status(Status.ERROR) else status (Status.SUCCESS)
                 callback.onResult(memes)

             }
           else if (searchKeyword != null) {
                val memes = repository.searchMemes(searchKeyword )

                Timber.e("loaded: ${memes.size} memes")

                if (memes.isEmpty()) status(Status.ERROR) else status (Status.SUCCESS)
                callback.onResult(memes)
            }
            else {
                val memes = repository.fetchMemes()

                Timber.e("loaded: ${memes.size} memes")

                if (memes.isEmpty()) status(Status.ERROR) else status (Status.SUCCESS)
                callback.onResult(memes)
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<ItemViewModel>) {
        if (params.key != user?.id) {
            scope.launch {
                if (user == null) {
                    val memes =  repository.fetchMemes(loadAfter = params.key)
                    callback.onResult(memes)
                } else {
                    val memes = repository.fetchMemesByUser(userId = user.id, loadAfter = params.key)
                    callback.onResult(memes)
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<ItemViewModel>) {}

    override fun getKey(item: ItemViewModel): String = item.id

}