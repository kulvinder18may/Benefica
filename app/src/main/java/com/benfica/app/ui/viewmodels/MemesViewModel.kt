package com.panchayat.takoli.ui.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.panchayat.takoli.data.Result
import com.panchayat.takoli.data.Status
import com.panchayat.takoli.data.datasource.FavesDataSource
import com.panchayat.takoli.data.datasource.MemesDataSource
import com.panchayat.takoli.data.datasource.PendingMemesDataSource
import com.panchayat.takoli.data.models.Fave
import com.panchayat.takoli.data.models.Meme
import com.panchayat.takoli.data.models.PendingMeme
import com.panchayat.takoli.data.models.Report
import com.panchayat.takoli.data.repositories.MemesRepository
import com.panchayat.takoli.data.responses.GenericResponse
import com.panchayat.takoli.data.responses.MemesResponse
import com.panchayat.takoli.data.wrappers.ItemViewModel
import com.panchayat.takoli.data.wrappers.ObservableUser
import com.panchayat.takoli.ui.callbacks.VideoUrlCallback
import kotlinx.coroutines.launch

class MemesViewModel constructor(private val repository: MemesRepository) : ViewModel() {
    private val _genericResponseLiveData = MutableLiveData<GenericResponse>()
    val genericResponseLiveData: MutableLiveData<GenericResponse>
        get() = _genericResponseLiveData

    private val _memeResponseLiveData = MutableLiveData<MemesResponse>()
    val memeResponseLiveData: MutableLiveData<MemesResponse>
        get() = _memeResponseLiveData

   private val _urlResponse= MutableLiveData<String>()
    val urlResponse: MutableLiveData<String>
        get() = _urlResponse

    private val _showStatusLiveData = MutableLiveData<Status>()
    val showStatusLiveData: MutableLiveData<Status>
        get() = _showStatusLiveData

    /**
     * Function to post new Meme
     * @param imageUri - Uri of the selected meme
     * @param meme - Meme model
     */
    fun getvideoUrl(imageUri: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            repository.getMemeVideo(imageUri,object:VideoUrlCallback{
                override fun onVideoUrl(url: String) {
                    _urlResponse.postValue(url)
                }
            })
        }
    }
    /**
     * Function to post new Meme
     * @param imageUri - Uri of the selected meme
     * @param meme - Meme model
     */
    fun postMeme(imageUri: Uri, meme: Meme) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            repository.postMeme(imageUri, meme) {
                when (it) {
                    is Result.Success -> {
                        _genericResponseLiveData.postValue(GenericResponse.success(it.data))
                    }

                    is Result.Error -> {
                        _genericResponseLiveData.postValue(GenericResponse.error(it.error))
                    }
                }
            }
        }
    }

    /**
     * Function to post pending Meme
     * @param oldId - ID of the pending meme
     * @param meme - Meme model
     */
    fun postPendingMeme(oldId: String, meme: Meme) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.postPendingMeme(oldId, meme)) {
                is Result.Success -> {
                    _genericResponseLiveData.postValue(GenericResponse.success(true,
                            item = GenericResponse.ITEM_RESPONSE.POST_MEME,
                            value = result.data))
                }

                is Result.Error -> {
                    _genericResponseLiveData.postValue(GenericResponse.error(result.error))
                }
            }
        }
    }

    /**
     * Function to fetch memes
     */
    fun fetchMemes(): LiveData<PagedList<ItemViewModel>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = MemesDataSource.Factory(repository, viewModelScope) {
            _showStatusLiveData.postValue(it)
        }
        return LivePagedListBuilder<String, ItemViewModel>(memeFactory, pagingConfig).build()
    }

    /**
     * Function to fetch memes
     */
    fun searchMemes(keyWord:String): LiveData<PagedList<ItemViewModel>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = MemesDataSource.Factory(repository, viewModelScope,searchKeyword=keyWord) {
            _showStatusLiveData.postValue(it)
        }
        return LivePagedListBuilder<String, ItemViewModel>(memeFactory, pagingConfig).build()
    }

    /**
     * Function to fetch memes
     */
    fun videoMemes(keyWord:String): LiveData<PagedList<ItemViewModel>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = MemesDataSource.Factory(repository, viewModelScope,searchKeyword=keyWord,isVideoOnly = true) {
            _showStatusLiveData.postValue(it)
        }
        return LivePagedListBuilder<String, ItemViewModel>(memeFactory, pagingConfig).build()
    }
    /**
     * Function to fetch memes
     */
    fun fetchFaves(): LiveData<PagedList<Fave>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val faveFactory = FavesDataSource.Factory(repository, viewModelScope) {
            _showStatusLiveData.postValue(it)
        }
        return LivePagedListBuilder<String, Fave>(faveFactory, pagingConfig).build()
    }

    /**
     * Function to fetch memes
     */
    fun fetchMemesByUser(user: ObservableUser): LiveData<PagedList<ItemViewModel>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = MemesDataSource.Factory(repository, viewModelScope, user) {
            _showStatusLiveData.postValue(it)
        }
        return LivePagedListBuilder<String, ItemViewModel>(memeFactory, pagingConfig).build()
    }

    /**
     * Function to fetch meme
     */
    fun fetchMeme(memeId: String) {
        viewModelScope.launch {
            _memeResponseLiveData.value = MemesResponse.loading()

            when (val result = repository.fetchMeme(memeId)) {
                is Result.Success -> {
                    _memeResponseLiveData.value = MemesResponse.success(result.data)
                }

                is Result.Error -> {
                    _memeResponseLiveData.value = MemesResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to fetch pending memes
     */
    fun fetchPendingMemes(): LiveData<PagedList<PendingMeme>> {
        val pagingConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .build()

        val memeFactory = PendingMemesDataSource.Factory(repository, viewModelScope) {
            _showStatusLiveData.postValue(it)
        }
        return LivePagedListBuilder<String, PendingMeme>(memeFactory, pagingConfig).build()
    }

    /**
     * Function to like meme
     * @param memeId - ID of the meme
     */
    fun likeMeme(memeId: String, userId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.likeMeme(memeId, userId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to like meme
     * @param memeId - ID of the meme
     */
    fun faveMeme(memeId: String, userId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.faveMeme(memeId, userId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data, GenericResponse.ITEM_RESPONSE.FAVE_MEME)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete meme
     * @param memeId - ID of the meme to delete
     */
    fun deleteMeme(memeId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.deleteMeme(memeId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.DELETE_MEME,
                            value = memeId)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete pending meme
     * @param memeId - ID of the meme to delete
     */
    fun deletePendingMeme(memeId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.updatePendingMeme(memeId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.DELETE_MEME,
                            value = memeId)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete meme
     * @param report
     */
    fun reportMeme(report: Report) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.reportMeme(report)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.REPORT_MEME)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

    /**
     * Function to delete fave
     * @param faveId - ID of the fave
     * @param userId - ID of the logged in User
     */
    fun deleteFave(faveId: String, userId: String) {
        viewModelScope.launch {
            _genericResponseLiveData.value = GenericResponse.loading()

            when (val result = repository.deleteFave(faveId, userId)) {
                is Result.Success -> {
                    _genericResponseLiveData.value = GenericResponse.success(result.data,
                            item = GenericResponse.ITEM_RESPONSE.DELETE_FAVE,
                            value = faveId)
                }

                is Result.Error -> {
                    _genericResponseLiveData.value = GenericResponse.error(result.error)
                }
            }
        }
    }

}