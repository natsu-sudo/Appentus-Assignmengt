package com.assignment.appentus.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.assignment.appentus.R
import com.assignment.appentus.pojo.ImageURL
import com.bumptech.glide.Glide

class ImageAdapter: PagingDataAdapter<ImageURL, ImageAdapter.ViewHolder>(DiffCallBack()) {
    class ViewHolder(private val view: View):RecyclerView.ViewHolder(view) {


        private val imageView:ImageView=itemView.findViewById(R.id.image_poster)
        private val title:TextView=itemView.findViewById(R.id.image_title)


        fun onBind(item: ImageURL) {
            Glide.with(itemView)
                    .load(item.downloadUrl)
                    .error(R.drawable.ic_launcher_background)
                    .override(500,500)
                    .into(imageView)
            title.text=item.author
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.onBind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_photo, parent, false)
        return ViewHolder(view)
    }
}

class DiffCallBack:DiffUtil.ItemCallback<ImageURL>() {
    override fun areItemsTheSame(oldItem: ImageURL, newItem: ImageURL): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageURL, newItem: ImageURL): Boolean {
        return oldItem == newItem
    }
}