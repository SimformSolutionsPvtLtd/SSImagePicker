package com.app.imagepickerlibrary.listener

import android.net.Uri
import com.app.imagepickerlibrary.model.PickerType

/**
 * Result listener for the image picker.
 */
interface ImagePickerResultListener {
    fun onImagePick(uri: Uri?, pickerType: PickerType)
    fun onMultiImagePick(uris: List<Uri>?, pickerType: PickerType)
}