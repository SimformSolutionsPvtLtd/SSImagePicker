package com.app.imagepickerlibrary.model

import android.net.Uri
import android.os.Parcelable
import android.provider.MediaStore
import com.app.imagepickerlibrary.MAX_PICK_LIMIT
import com.app.imagepickerlibrary.toByteSize
import kotlinx.parcelize.Parcelize
import java.util.Locale

/**
 * PickerConfig to manage the picker. All the options are modified by ImagePicker builder.
 */
@Parcelize
internal data class PickerConfig(
    var pickerType: PickerType = PickerType.GALLERY,
    var pickerTitle: String = "Image Picker",
    var showCountInToolBar: Boolean = true,
    var showFolders: Boolean = true,
    var allowMultipleSelection: Boolean = false,
    var maxPickCount: Int = MAX_PICK_LIMIT,
    var maxPickSizeMB: Float = Float.MAX_VALUE,
    var pickExtension: PickExtension = PickExtension.ALL,
    var showCameraIconInGallery: Boolean = true,
    var isDoneIcon: Boolean = true,
    var openCropOptions: Boolean = false,
    var openSystemPicker: Boolean = false,
    var compressImage: Boolean = false,
    var compressQuality: Int = 75
) : Parcelable {

    companion object {
        fun defaultPicker(): PickerConfig {
            return PickerConfig()
        }
    }

    /**
     * Function to generate selection arguments for content resolver based on size and extension
     */
    fun generateSelectionArguments(): Pair<String, Array<String>> {
        val selection: StringBuilder = StringBuilder()
        val selectionArgs = mutableListOf<String>()
        if (maxPickSizeMB != Float.MAX_VALUE) {
            selection.append("${MediaStore.Images.Media.SIZE} <= ?")
            selectionArgs.add(maxPickSizeMB.toByteSize().toString())
        }
        if (pickExtension != PickExtension.ALL) {
            if (selection.isNotEmpty()) {
                selection.append(" and ")
            }
            selection.append("${MediaStore.Images.Media.MIME_TYPE} =?")
            selectionArgs.add("image/${pickExtension.name.lowercase(Locale.getDefault())}")
        }
        return Pair(selection.toString(), selectionArgs.toTypedArray())
    }
}

/**
 * Type of picker that user wants to open.
 * User can also show the camera icon in GALLERY picker mode.
 */
enum class PickerType { GALLERY, CAMERA }

/**
 * Type of image that user wants to select.
 * By default ALL image will be displayed.
 */
enum class PickExtension {
    PNG, JPEG, WEBP, ALL;

    /**
     * Internal function to get mime type of picked extension for the system launcher.
     */
    internal fun getMimeType(): String {
        return when (this) {
            PNG -> "image/png"
            JPEG -> "image/jpeg"
            WEBP -> "image/webp"
            ALL -> "image/*"
        }
    }
}

/**
 * ImageProvider constants to determine user has selected which picker option from the bottom sheet.
 * This class is only related to bottomsheet which displays the picker options.
 */
enum class ImageProvider {
    GALLERY,
    CAMERA,
    NONE
}

/**
 * This class represent an image in the picker.
 */
@Parcelize
internal data class Image(
    val id: Long,
    val uri: Uri,
    val name: String,
    val bucketId: Long,
    val bucketName: String,
    val size: Long,
    var isSelected: Boolean = false
) : Parcelable

/**
 * This class represent a folder in the picker.
 * Folders are created on the bases of @param[bucketId]
 */
@Parcelize
internal data class Folder(
    val bucketId: Long,
    val bucketName: String,
    val uri: Uri,
    val images: List<Image>
) : Parcelable

/**
 * This sealed class represent a state for picker
 * It contains the error, loading and success as state.
 */
internal sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}