package com.rasel.flickergallery.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rasel.flickergallery.data.models.ImageListResponse
import com.rasel.flickergallery.data.repositories.FlickerImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: FlickerImageRepository) : ViewModel() {

    val imageListLiveData: LiveData<ImageListResponse?> = repository.imageListLiveData
    val error: LiveData<String?> = repository.error
    var currentQuery: String? = null
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(true)

    val searchImages = { query: String? ->
        if (!query.isNullOrEmpty()) {
            currentQuery = query
            getImages(query)
            isLoading.value = true
        }
    }

    fun getImages(query: String? = null) {
        viewModelScope.launch {
            repository.getImages(query)
        }
    }
}
