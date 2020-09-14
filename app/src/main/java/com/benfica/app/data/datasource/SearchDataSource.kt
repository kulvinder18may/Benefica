package com.benfica.app.data.datasource

import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import com.benfica.app.data.Status
import com.benfica.app.data.models.Fave
import com.benfica.app.data.repositories.MemesRepository
import com.benfica.app.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class SearchDataSource constructor(private val repository: MemesRepository,
                                   private val scope: CoroutineScope,
                                   private val status: (Status) -> Unit): ItemKeyedDataSource<String, Fave>(), KoinComponent {

    private val sessionManager: SessionManager by inject()
    private val userId = sessionManager.getUserId()

    class Factory(private val repository: MemesRepository,
                  private val scope: CoroutineScope,
                  private val status: (Status) -> Unit): DataSource.Factory<String, Fave>() {
        override fun create(): DataSource<String, Fave> {
            return FavesDataSource(repository, scope, status)
        }
    }

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Fave>) {
        Timber.e("Loading initial...")

        scope.launch {
            val memes = repository.fetchFaves(userId)

            if (memes.isEmpty()) status(Status.ERROR) else status (Status.SUCCESS)
            callback.onResult(memes)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Fave>) {
        scope.launch {
            val memes = repository.fetchFaves(userId = userId, loadAfter = params.key)
            Timber.e("Faves fetched: ${memes.size}")
            callback.onResult(memes)
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Fave>) {
        scope.launch {
            val memes = repository.fetchFaves(userId = userId, loadBefore = params.key)
            Timber.e("Faves fetched: ${memes.size}")
            callback.onResult(memes)
        }
    }

    override fun getKey(item: Fave): String = item.id!!
}