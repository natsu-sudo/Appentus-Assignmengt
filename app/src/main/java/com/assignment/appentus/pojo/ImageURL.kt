package com.assignment.appentus.pojo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "image_urls")
data class ImageURL(
        @PrimaryKey
        val id:Long,
        val author:String,
        @ColumnInfo(name="download_url")
        @SerializedName("download_url")
        val downloadUrl:String)