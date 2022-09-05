package com.ssimagepicker.app.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssimagepicker.app.R
import com.ssimagepicker.app.databinding.ListItemImageDataBinding
import com.ssimagepicker.app.loadImage

/**
 * ImageDataAdapter class to display list of picked images from the picker.
 */
class ImageDataAdapter(private val imageList: List<Uri>) :
    RecyclerView.Adapter<ImageDataAdapter.ImageDataViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDataViewHolder {
        val binding: ListItemImageDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.list_item_image_data, parent, false
        )
        return ImageDataViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageDataViewHolder, position: Int) {
        holder.binding.imageView.loadImage(imageList[position])
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageDataViewHolder(val binding: ListItemImageDataBinding) :
        RecyclerView.ViewHolder(binding.root)
}