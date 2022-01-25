package com.codingnight.example.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData

const val FAKE_PAGE_SIZE: Int = 30

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    private val factory = PixabayDataSourceFactory(application)

    val pageListLiveData = factory.toLiveData(FAKE_PAGE_SIZE)

    val networkStatus: LiveData<NetworkStatus> = Transformations.switchMap(factory.pixabayDataSource) { it.networkStatus }

    fun resetQuery() {
        pageListLiveData.value?.dataSource?.invalidate()
    }

    fun retry() {
        factory.pixabayDataSource.value?.retry?.invoke()
    }
}