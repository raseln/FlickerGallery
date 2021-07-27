package com.rasel.flickergallery.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rasel.flickergallery.data.repositories.FlickerImageRepository
import javax.inject.Inject

class ViewModelFactory @Inject constructor(private val flickerRepo: FlickerImageRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(flickerRepo) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}