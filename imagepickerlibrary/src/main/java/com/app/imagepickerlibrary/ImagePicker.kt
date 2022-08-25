package com.app.imagepickerlibrary

import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.PickExtension
import com.app.imagepickerlibrary.model.PickerConfig
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.activity.ImagePickerActivity
import com.app.imagepickerlibrary.util.isAtLeast13
import java.lang.Integer.min

class ImagePicker private constructor(private val callback: ImagePickerResultListener) {
    private val pickerConfig = PickerConfig()

    /**
     * ImagePickerActivity tool bar title
     */
    fun title(title: String): ImagePicker {
        check(title.isNotBlank() && title.isNotEmpty()) { "Title text should not be empty" }
        pickerConfig.pickerTitle = title
        return this
    }

    /**
     * Whether to display selected count in toolbar or not.
     */
    fun showCountInToolBar(enable: Boolean): ImagePicker {
        pickerConfig.showCountInToolBar = enable
        return this
    }

    /**
     * Whether to open image picker in folder mode or in image list mode.
     */
    fun showFolder(show: Boolean): ImagePicker {
        pickerConfig.showFolders = show
        return this
    }

    /**
     * Whether to allow multiple selection or not
     * If only multiple selection is enabled and no value is passed for maxcount then the default pick size will be Int.MAX_VALUE
     */
    fun multipleSelection(enable: Boolean): ImagePicker {
        multipleSelection(enable, Int.MAX_VALUE)
        return this
    }

    /**
     * Whether to allow multiple selection or not
     * Pass max count to allow maximum number to pick from the picker.
     */
    fun multipleSelection(enable: Boolean, maxCount: Int): ImagePicker {
        if (enable) {
            check(maxCount > 0) { "The maximum allowed image count should be greater than 0" }
            pickerConfig.maxPickCount = maxCount
        }
        pickerConfig.allowMultipleSelection = enable
        return this
    }

    /**
     * Maximum allowed size for image in mb. All the images whose size is less then this will be displayed.
     * By default all the images are displayed.
     */
    fun maxImageSize(maxSizeMB: Int): ImagePicker {
        check(maxSizeMB > 0) { "The maximum allowed size should be greater than 0" }
        pickerConfig.maxPickSizeMB = maxSizeMB
        return this
    }

    /**
     * Pass the type of extension that needs to be displayed
     * By default PickExtension.ALL is selected so that all the images are displayed.
     */
    fun extension(pickExtension: PickExtension): ImagePicker {
        pickerConfig.pickExtension = pickExtension
        return this
    }

    /**
     * Whether to show camera icon in gallery picker mode or not.
     * The camera icon will open the camera and once the image is captured it will be passed to user via callback
     */
    fun cameraIcon(enable: Boolean): ImagePicker {
        pickerConfig.showCameraIconInGallery = enable
        return this
    }

    /**
     * Whether the done button is icon or text.
     * The styling for both icon and text can be changed via style options.
     * It will be only displayed if multiple selection mode is enabled.
     */
    fun doneStyle(isDoneIcon: Boolean): ImagePicker {
        pickerConfig.isDoneIcon = isDoneIcon
        return this
    }

    /**
     * Whether to open cropping option or not.
     * The cropping option are only available if the single selection is set or the picture is picked via camera.
     */
    fun allowCropping(enable: Boolean): ImagePicker {
        pickerConfig.openCropOptions = enable
        return this
    }

    /**
     * Whether to open new photo picker for android 13+ or not.
     * If the system picker is set to open the all other options except multi selection, max count and pick extension are ignored.
     * The max count for picking image depends on the OS, you can get the maximum images count via MediaStore.getPickImagesMaxLimit().
     * SSImagePicker automatically manages the max pick count for the system picker.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun systemPicker(enable: Boolean): ImagePicker {
        pickerConfig.openSystemPicker = enable
        return this
    }

    /**
     * Whether to compress image or not. And the quality of compression
     * If this is enabled for single selection the compress is done via UCrop library.
     * All the image captured via camera in any picker mode will be handled via UCrop library.
     * If the crop option is disabled but the compression is enabled then the UCrop will be opened for only compression.
     * For multiple selection the height and width of images are divided by 1.5 to scale down the image and the quality is applied on that scaled down image
     */
    fun compressImage(enable: Boolean, quality: Int = 75): ImagePicker {
        if (enable) {
            check(quality > 0) { "The compress quality should be greater than 0" }
            pickerConfig.compressQuality = quality
        }
        pickerConfig.compressImage = enable
        return this
    }

    fun open(pickerType: PickerType, fragment: Fragment) {
        open(pickerType, fragment.requireActivity())
    }

    /**
     * Opens either system picker or the ImagePickerActivity activity depending on the configuration.
     * If the system picker is selected for android 13+ and the picker type is camera then the ImagePickerActivity is opened in camera mode.
     * The system picker is only available in android 13+.
     */
    fun open(pickerType: PickerType, activity: ComponentActivity) {
        pickerConfig.pickerType = pickerType
        val picker = activity.registerActivityResult("image-picker") {
            it.getImages(pickerConfig.allowMultipleSelection, callback)
        }
        if (pickerType == PickerType.GALLERY && pickerConfig.openSystemPicker && isAtLeast13()) {
            openSystemPhotoPicker(picker)
        } else {
            openImagePicker(activity, picker)
        }
    }

    /**
     * Open the ImagePickerActivity with all the required options
     */
    private fun openImagePicker(
        activity: ComponentActivity,
        picker: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(activity, ImagePickerActivity::class.java)
        intent.putExtra(EXTRA_IMAGE_PICKER_CONFIG, pickerConfig)
        picker.launch(intent)
    }

    /**
     * Opens the system picker for android 13+
     * Only multiple selection, max pic count and pick extension are considered for default picker.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openSystemPhotoPicker(picker: ActivityResultLauncher<Intent>) {
        //Open new photo picker from system for android 13 and above
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        if (pickerConfig.allowMultipleSelection) {
            //There is limit for maximum number of image from default picker
            //https://developer.android.com/about/versions/13/features/photopicker#select_multiple_photos_or_videos
            val pickCount = min(MediaStore.getPickImagesMaxLimit(), pickerConfig.maxPickCount)
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, pickCount)
        }
        intent.type = pickerConfig.pickExtension.getMimeType()
        picker.launch(intent)
    }

    companion object {
        fun with(callback: ImagePickerResultListener): ImagePicker {
            return ImagePicker(callback)
        }
    }
}