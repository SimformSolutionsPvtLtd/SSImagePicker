package com.app.imagepickerlibrary.ui.adapter

import androidx.databinding.ViewDataBinding
import com.app.imagepickerlibrary.R
import com.app.imagepickerlibrary.databinding.ListItemFolderBinding
import com.app.imagepickerlibrary.listener.ItemClickListener
import com.app.imagepickerlibrary.model.Folder

/**
 * FolderAdapter class to display folder items.
 */
internal class FolderAdapter(listener: ItemClickListener<Folder>) : BaseAdapter<Folder>(listener) {
    override fun getLayoutId(): Int = R.layout.list_item_folder

    override fun setDataForListItemWithPosition(
        binding: ViewDataBinding,
        data: Folder,
        adapterPosition: Int
    ) {
        super.setDataForListItemWithPosition(binding, data, adapterPosition)
        (binding as ListItemFolderBinding).folder = data
    }
}