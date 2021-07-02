package com.assignment.appentus.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.assignment.appentus.R
import com.assignment.appentus.pojo.ImageURL
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.card.MaterialCardView

class ImageAdapter:ListAdapter <ImageURL,ImageAdapter.ViewHolder> (DiffCallBack()){
    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {


        private val imageView:ImageView=itemView.findViewById(R.id.image_poster)
        private val materialCardView:MaterialCardView=itemView.findViewById(R.id.card_view)
        private val title:TextView=itemView.findViewById(R.id.image_title)
        private val shimmer=itemView.findViewById<ShimmerFrameLayout>(R.id.shimmer_layout)


        fun onBind(item: ImageURL) {
            shimmer.startShimmer()
            Glide.with(itemView)
                    .load(item.downloadUrl)
                .listener(object :RequestListener<Drawable>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        shimmer.stopShimmer()
                        shimmer.setShimmer(null)
                        shimmer.hideShimmer()
                        materialCardView.visibility=View.VISIBLE
                        imageView.setImageResource(R.drawable.ic_launcher_background)
                        return false
                    }
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        shimmer.stopShimmer()
                        shimmer.setShimmer(null)
                        shimmer.hideShimmer()
                        materialCardView.visibility=View.VISIBLE
                        shimmer.visibility=View.GONE
                        return false
                    }
                })
                    .override(150,150)
                    .into(imageView)
            title.text= (absoluteAdapterPosition+1).toString()


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.list_photo,parent,false)
        return ViewHolder((view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

}

class DiffCallBack:DiffUtil.ItemCallback<ImageURL>() {
    override fun areItemsTheSame(oldItem: ImageURL, newItem: ImageURL): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: ImageURL, newItem: ImageURL): Boolean {
        return oldItem.id == newItem.id
    }
}