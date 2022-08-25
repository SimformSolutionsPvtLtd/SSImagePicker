package com.app.imagepickerlibrary.util

import android.net.Uri
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.app.imagepickerlibrary.model.Folder

/**
 * Binding adapter function to show image uri via xml it self.
 */
@BindingAdapter("android:src")
internal fun loadImage(imageView: AppCompatImageView, uri: Uri) {
    imageView.load(uri) {
        crossfade(true)
    }
}

/**
 * Binding adapter function to show folder name with images count
 */
@BindingAdapter("folderName")
internal fun setFolderName(textView: TextView, folder: Folder) {
    textView.text = buildString {
        append(folder.bucketName)
        append(" ")
        append("(${folder.images.size})")
    }
}
