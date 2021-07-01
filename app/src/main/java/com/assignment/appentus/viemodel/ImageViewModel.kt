package com.assignment.appentus.viemodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.assignment.appentus.database.ImageURLRepository
import com.assignment.appentus.pojo.LoadingStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(context: Context) : ViewModel() {
    private val repository = ImageURLRepository(context)
    private var liveStatus = MutableLiveData<LoadingStatus>()

    fun fetchFromNetwork(pageNumber: Int) {
        Log.d("TAG", "fetchFromNetwork: ")
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.fetchFromNetwork(pageNumber)
            }!!
        }
    }

    val imageURL = Pager(PagingConfig(
            pageSize = 10,
            enablePlaceholders = true)){
        repository.getImageUrlsFromDb()
    }.flow


}