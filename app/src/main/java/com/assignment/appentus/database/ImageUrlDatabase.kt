package com.assignment.appentus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.assignment.appentus.pojo.ImageURL

@Database(entities = [ImageURL::class],version = 1)
abstract class ImageUrlDatabase:RoomDatabase() {
    abstract fun imageUrlDao(): ImageDao

    companion object{
        @Volatile
        private var instance:ImageUrlDatabase?=null

        fun getInstance(context: Context)= instance?: synchronized(this){
            Room.databaseBuilder(context.applicationContext,ImageUrlDatabase::class.java
                ,"urls_database").build().also {
                instance=it
            }
        }
    }

}