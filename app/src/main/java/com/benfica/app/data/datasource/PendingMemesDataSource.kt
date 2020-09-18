package com.mysqldatabase.app.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.mysqldatabase.app.data.Status
import com.mysqldatabase.app.data.models.PendingMeme
import com.mysqldatabase.app.data.repositories.MemesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import timber.log.Timber

class PendingMemesDataSource constructor(private val repository: MemesRepository,
                                          private val scope: CoroutineScope,
                                         private val status: (Status) -> Unit): ItemKeyedDataSource<String, PendingMeme>(), KoinComponent {

    class Factory(private val repository: MemesRepository,
                  private val scope: CoroutineScope,
                  private val status: (Status) -> Unit): DataSource.Factory<String, PendingMeme>() {
        override fun create(): DataSource<String, PendingMeme> {
            return PendingMemesDataSource(repository, scope, status)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<PendingMeme>) {
        Timber.e("Loading initial...")
        status(Status.LOADING)

        scope.launch {
            val memes = repository.fetchPendingMemes()
            if (memes.isEmpty()) status(Status.ERROR) else status(Status.SUCCESS)
            callback.onResult(memes)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<PendingMeme>) {
        scope.launch {
            val memes = repository.fetchPendingMemes(loadAfter = params.key)
            callback.onResult(memes)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<PendingMeme>) {
        scope.launch {
            val memes = repository.fetchPendingMemes(loadBefore = params.key)
            callback.onResult(memes)
        }
    }

    override fun getKey(item: PendingMeme): String = item.id!!
}