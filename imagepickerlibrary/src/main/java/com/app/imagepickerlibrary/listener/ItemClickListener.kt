package com.app.imagepickerlibrary.listener

import androidx.annotation.IdRes

/**
 * Item click listener for the recycler view.
 */
interface ItemClickListener<T> {
    /**
     * @param[item]  The data which needed to pass
     * @param[position]  The position of clicked item in recyclerview
     * @param[viewId]  The id of view which was clicked
     */
    fun onItemClick(item: T, position: Int, @IdRes viewId: Int)
}