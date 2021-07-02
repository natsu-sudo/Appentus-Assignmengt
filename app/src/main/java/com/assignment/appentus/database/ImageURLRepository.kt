package com.assignment.appentus.database

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.assignment.appentus.network.ApiService
import com.assignment.appentus.pojo.ErrorCode
import com.assignment.appentus.pojo.ImageURL
import com.assignment.appentus.pojo.LoadingStatus
import java.net.UnknownHostException


private const val TAG = "ImageURLRepository"

class ImageURLRepository(context: Context) {
    private val urlImageDao = ImageUrlDatabase.getInstance(context).imageUrlDao()
    private val apiService by lazy {
        ApiService.getInstance()
    }

    suspend fun fetchFromNetwork(pageNumber:Int) = try {
        val result = apiService.getImageList(pageNumber)
        Log.d(TAG, "fetchFromNetwork: $result")
        if (result.isSuccessful) {
            val imageList = result.body()
            Log.d(TAG, "fetchFromNetwork: $imageList")
            imageList?.let {
                Log.d(TAG, "fetchFromNetwork: $it")
            urlImageDao.insertIntoDb(it)
            }
            LoadingStatus.success()
        } else {
            LoadingStatus.error(ErrorCode.NO_DATA)
        }
    } catch (ex: UnknownHostException) {
        LoadingStatus.error(ErrorCode.NETWORK_ERROR)
    } catch (ex: Exception) {
        LoadingStatus.error(ErrorCode.UNKNOWN_ERROR)
    }

    fun getImageUrlsFromDb(): LiveData<List<ImageURL>> {
        return urlImageDao.getListOfImage()
    }

    fun getTotalCount(): LiveData<Int> {
        return urlImageDao.getCount()
    }

    suspend fun deleteAll(){
        return urlImageDao.deleteAllData()
    }

}