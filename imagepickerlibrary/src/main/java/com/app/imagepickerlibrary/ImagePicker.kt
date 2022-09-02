package com.app.imagepickerlibrary

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.PickExtension
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.activity.ImagePickerActivity
import com.app.imagepickerlibrary.util.PickerConfigManager
import com.app.imagepickerlibrary.util.isPhotoPickerAvailable
import java.lang.Integer.min

class ImagePicker private constructor(
    private val callback: ImagePickerResultListener,
    private val activity: ComponentActivity
) {
    private val picker = activity.registerActivityResult("image-picker") {
        val isMultipleSelection = pickerConfigManager.getPickerConfig().allowMultipleSelection
        it.getImages(isMultipleSelection, callback)
    }
    private val pickerConfigManager = PickerConfigManager(activity)

    /**
     * ImagePickerActivity tool bar title
     */
    fun title(title: String): ImagePicker {
        require(title.isNotBlank() && title.isNotEmpty()) { "Title text should not be empty" }
        pickerConfigManager.getPickerConfig().pickerTitle = title
        return this
    }

    /**
     * Whether to display selected count in toolbar or not.
     */
    fun showCountInToolBar(enable: Boolean): ImagePicker {
        pickerConfigManager.getPickerConfig().showCountInToolBar = enable
        return this
    }

    /**
     * Whether to open image picker in folder mode or in image list mode.
     */
    fun showFolder(show: Boolean): ImagePicker {
        pickerConfigManager.getPickerConfig().showFolders = show
        return this
    }

    /**
     * Whether to allow multiple selection or not
     * If only multiple selection is enabled and no value is passed for maxcount then the default pick size will be value of MAX_PICK_LIMIT = (15)
     */
    fun multipleSelection(enable: Boolean): ImagePicker {
        multipleSelection(enable, MAX_PICK_LIMIT)
        return this
    }

    /**
     * Whether to allow multiple selection or not
     * Pass max count to allow maximum number to pick from the picker.
     */
    fun multipleSelection(enable: Boolean, maxCount: Int): ImagePicker {
        val pickerConfig = pickerConfigManager.getPickerConfig()
        if (enable) {
            require(maxCount in 1..MAX_PICK_LIMIT) { "The maximum allowed image count should be in range of 1..$MAX_PICK_LIMIT. The end limit is inclusive." }
            pickerConfig.maxPickCount = maxCount
        }
        pickerConfig.allowMultipleSelection = enable
        return this
    }

    /**
     * Maximum allowed size for image in mb in float. All the images whose size is less then this will be displayed.
     * Only two decimal point place will be considered from the floating value.
     * By default all the images are displayed.
     */
    fun maxImageSize(maxSizeMB: Float): ImagePicker {
        require(maxSizeMB > 0) { "The maximum allowed size should be greater than 0" }
        pickerConfigManager.getPickerConfig().maxPickSizeMB = maxSizeMB
        return this
    }

    /**
     * Maximum allowed size for image in mb in integer. All the images whose size is less then this will be displayed.
     * By default all the images are displayed.
     */
    fun maxImageSize(maxSizeMB: Int): ImagePicker {
        require(maxSizeMB > 0) { "The maximum allowed size should be greater than 0" }
        pickerConfigManager.getPickerConfig().maxPickSizeMB = maxSizeMB.toFloat()
        return this
    }

    /**
     * Pass the type of extension that needs to be displayed
     * By default PickExtension.ALL is selected so that all the images are displayed.
     */
    fun extension(pickExtension: PickExtension): ImagePicker {
        pickerConfigManager.getPickerConfig().pickExtension = pickExtension
        return this
    }

    /**
     * Whether to show camera icon in gallery picker mode or not.
     * The camera icon will open the camera and once the image is captured it will be passed to user via callback
     */
    fun cameraIcon(enable: Boolean): ImagePicker {
        pickerConfigManager.getPickerConfig().showCameraIconInGallery = enable
        return this
    }

    /**
     * Whether the done button is icon or text.
     * The styling for both icon and text can be changed via style options.
     * If it is enable the icon is displayed if it is disabled the text is displayed.
     * It will be only displayed if multiple selection mode is enabled.
     */
    fun doneIcon(enable: Boolean): ImagePicker {
        pickerConfigManager.getPickerConfig().isDoneIcon = enable
        return this
    }

    /**
     * Whether to open cropping option or not.
     * The cropping option are only available if the single selection is set or the picture is picked via camera.
     */
    fun allowCropping(enable: Boolean): ImagePicker {
        pickerConfigManager.getPickerConfig().openCropOptions = enable
        return this
    }

    /**
     * Whether to open new photo picker for android 11+ or not.
     * If the system picker is set to open the all other options except multi selection, max count and pick extension are ignored.
     * The max count for picking image depends on the OS, you can get the maximum images count via MediaStore.getPickImagesMaxLimit().
     * SSImagePicker automatically manages the max pick count for the system picker.
     * The system picker is always available on android 13+. It is although available on android 11+ if the criteria is matched.
     * The criteria are as follow [More Details](https://developer.android.com/training/data-storage/shared/photopicker)
     * 1. Run Android 11 (API level 30) or higher
     * 2. Receive changes to Modular System Components through Google System Updates
     */
    @RequiresApi(Build.VERSION_CODES.R)
    fun systemPicker(enable: Boolean): ImagePicker {
        pickerConfigManager.getPickerConfig().openSystemPicker = enable
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
        val pickerConfig = pickerConfigManager.getPickerConfig()
        if (enable) {
            require(quality in 1..100) { "The compress quality should be greater than 0 and less than or equal to 100" }
            pickerConfig.compressQuality = quality
        }
        pickerConfig.compressImage = enable
        return this
    }

    /**
     * Opens either system picker or the ImagePickerActivity activity depending on the configuration.
     * If the system picker is selected for android 11+ and the picker type is camera then the ImagePickerActivity is opened in camera mode.
     * The system picker is only available in android 11+ with some that meets some criteria set by system.
     * [More Details](https://developer.android.com/training/data-storage/shared/photopicker)
     */
    fun open(pickerType: PickerType) {
        val pickerConfig = pickerConfigManager.getPickerConfig()
        pickerConfig.pickerType = pickerType
        if (pickerType == PickerType.GALLERY && pickerConfig.openSystemPicker && isPhotoPickerAvailable()) {
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
        intent.putExtra(EXTRA_IMAGE_PICKER_CONFIG, pickerConfigManager.getPickerConfig())
        picker.launch(intent)
    }

    /**
     * Opens the system picker for android 11+
     * Only multiple selection, max pic count and pick extension are considered for default picker.
     */
    @RequiresApi(Build.VERSION_CODES.R)
    private fun openSystemPhotoPicker(picker: ActivityResultLauncher<Intent>) {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        if (pickerConfigManager.getPickerConfig().allowMultipleSelection) {
            intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, getMaxItems())
        }
        intent.type = pickerConfigManager.getPickerConfig().pickExtension.getMimeType()
        picker.launch(intent)
    }

    /**
     * Returns the maximum count to pick images from the system picker.
     * There is a platform limit on the Android 13+ which limits the number of maximum pick count
     * [More Details](https://developer.android.com/training/data-storage/shared/photopicker#select_multiple_media_items_2)
     */
    @SuppressLint("NewApi")
    private fun getMaxItems(): Int {
        val pickerConfig = pickerConfigManager.getPickerConfig()
        return if (isPhotoPickerAvailable()) {
            min(MediaStore.getPickImagesMaxLimit(), pickerConfig.maxPickCount)
        } else {
            pickerConfig.maxPickCount
        }
    }

    companion object {
        fun ComponentActivity.registerImagePicker(callback: ImagePickerResultListener): ImagePicker {
            return ImagePicker(callback, this)
        }

        fun Fragment.registerImagePicker(callback: ImagePickerResultListener): ImagePicker {
            return ImagePicker(callback, requireActivity())
        }
    }
}