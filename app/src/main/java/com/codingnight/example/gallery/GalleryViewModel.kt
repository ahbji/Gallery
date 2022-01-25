package com.codingnight.example.gallery

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.toLiveData

val FAKE_PAGE_SIZE: Int = 30

class GalleryViewModel(application: Application) : AndroidViewModel(application) {
    val pageListLiveData = PixabayDataSourceFactory(application).toLiveData(FAKE_PAGE_SIZE)
    fun resetQuery() {
        pageListLiveData.value?.dataSource?.invalidate()
    }
}