package com.assignment.appentus.database

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.assignment.appentus.pojo.ImageURL

@Dao
interface ImageDao {

    @Query("select * from image_urls order by id asc")
    fun getListOfImage():LiveData<List<ImageURL>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIntoDb(list: List<ImageURL>)

    @Delete
    suspend fun delete(imageURL: ImageURL)

    @Query("delete from image_urls")
    suspend fun deleteAllData()

    @Query("SELECT COUNT(*) FROM image_urls")
    fun getCount(): LiveData<Int>

}