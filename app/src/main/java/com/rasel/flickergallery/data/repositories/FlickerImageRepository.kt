package com.rasel.flickergallery.data.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rasel.flickergallery.adapters.DateJsonAdapter
import com.rasel.flickergallery.data.api.FlickerApiService
import com.rasel.flickergallery.data.models.ImageListResponse
import com.rasel.flickergallery.di.IoDispatcher
import com.rasel.flickergallery.utils.debugLogInfo
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlickerImageRepository @Inject constructor(
    private val apiService: FlickerApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    private val _imageListLiveData = MutableLiveData<ImageListResponse?>()
    val imageListLiveData: LiveData<ImageListResponse?> = _imageListLiveData

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    suspend fun getImages(tags: String?) {
        withContext(dispatcher) {
            try {
                val response = apiService.getImages(tags)
                if (response.isSuccessful) {
                    response.body()?.let { responseStr ->
                        var parsedStr = responseStr.subSequence(0, responseStr.length - 1).toString()
                        parsedStr = parsedStr.replace("jsonFlickrFeed(", "")
                        parseJson(parsedStr)
                    }
                    _error.postValue(null)
                } else {
                    _imageListLiveData.postValue(null)
                    _error.postValue(response.errorBody()?.toString() ?: "Something went wrong! Try again!")
                }
            } catch (exception: Exception) {
                debugLogInfo("Error: $exception")
                _imageListLiveData.postValue(null)
                _error.postValue(exception.message ?: "Something went wrong! Try again!")
            }
        }
    }

    private fun parseJson(json: String) {
        val moshi = Moshi.Builder().add(Date::class.java, DateJsonAdapter()).build()
        val jsonAdapter = moshi.adapter(ImageListResponse::class.java)
        val parsedResponse = jsonAdapter.fromJson(json)
        _imageListLiveData.postValue(parsedResponse)
    }
}