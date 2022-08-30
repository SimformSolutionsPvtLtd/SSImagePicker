package com.app.imagepickerlibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.imagepickerlibrary.listener.ItemClickListener

/**
 * BaseAdapter class to manage ImageAdapter and FolderAdapter
 * All the common functionalities related to recycler view item are implemented here.
 */
internal abstract class BaseAdapter<T>(protected val listener: ItemClickListener<T>) :
    RecyclerView.Adapter<BaseAdapter<T>.BaseVH>() {
    protected val itemList = mutableListOf<T>()

    @LayoutRes
    abstract fun getLayoutId(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, getLayoutId(), parent, false)
        return BaseVH(binding)
    }

    override fun onBindViewHolder(holder: BaseVH, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount() = itemList.size

    /**
     * Clearing previous image list item and adding all the new items
     * Using notifyDataSetChanged to manage both addition and removal of image items from list
     */
    fun setItemList(list: List<T>) {
        itemList.clear()
        itemList.addAll(list)
        notifyDataSetChanged()
    }

    internal open inner class BaseVH(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.onItemClick(
                    itemList[absoluteAdapterPosition],
                    absoluteAdapterPosition,
                    it.id
                )
            }
        }

        /**
         * This function is used to bind recycler data particular item wise.
         */
        fun bind(data: T) {
            setDataForListItemWithPosition(binding, data, absoluteAdapterPosition)
            binding.executePendingBindings()
        }
    }

    /**
     * This function is used to set data to list item.
     */
    open fun setDataForListItemWithPosition(
        binding: ViewDataBinding,
        data: T,
        adapterPosition: Int
    ) {
    }
}