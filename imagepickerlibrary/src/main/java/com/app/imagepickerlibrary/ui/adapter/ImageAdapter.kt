package com.app.imagepickerlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.app.imagepickerlibrary.R
import com.app.imagepickerlibrary.databinding.ListItemImageBinding
import com.app.imagepickerlibrary.listener.ItemClickListener
import com.app.imagepickerlibrary.model.Image

/**
 * ImageAdapter class to display image items.
 */
internal class ImageAdapter(listener: ItemClickListener<Image>) : BaseAdapter<Image>(listener) {
    override fun getLayoutId(): Int = R.layout.list_item_image

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ListItemImageBinding =
            DataBindingUtil.inflate(inflater, getLayoutId(), parent, false)
        return ImageVH(binding)
    }

    override fun setDataForListItemWithPosition(
        binding: ViewDataBinding,
        data: Image,
        adapterPosition: Int
    ) {
        super.setDataForListItemWithPosition(binding, data, adapterPosition)
        (binding as ListItemImageBinding).image = data
        binding.checkMark.isVisible = data.isSelected
    }

    internal inner class ImageVH(binding: ListItemImageBinding) : BaseVH(binding) {
        init {
            binding.imageZoom.setOnClickListener {
                listener.onItemClick(
                    itemList[absoluteAdapterPosition],
                    absoluteAdapterPosition,
                    it.id
                )
            }
        }
    }
}