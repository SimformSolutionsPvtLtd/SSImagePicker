package com.app.imagepickerlibrary

import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.TypedValue
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.AttrRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.Image
import com.app.imagepickerlibrary.model.PickerConfig
import com.app.imagepickerlibrary.util.isAtLeast13
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


/**
 * Checks whether the any camera activity is available or not to handle the intent.
 * If there is camera activity open the camera
 */
internal fun Context.dispatchTakePictureIntent(onGetImageFromCameraActivityResult: ActivityResultLauncher<Intent>): Uri? {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        packageManager?.run {
            takePictureIntent.resolveActivity(this)?.also {
                try {
                    val timeStamp: String = SimpleDateFormat(
                        dateFormatForTakePicture,
                        Locale.getDefault()
                    ).format(Date())
                    createImageFile(timeStamp).apply {
                        var photoURI: Uri?
                        also { photo ->
                            photoURI = FileProvider.getUriForFile(
                                this@dispatchTakePictureIntent,
                                "${applicationContext.packageName}.${BuildConfig.LIBRARY_PACKAGE_NAME}.provider",
                                photo
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            onGetImageFromCameraActivityResult.launch(takePictureIntent)
                        }
                        return photoURI
                    }
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    return null
                }
            }
        }
    }
    return null
}

/**
 * Create image file in the picture's directory of external files directory.
 */
internal fun Context.createImageFile(name: String = ""): File {
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${name}_", ".jpg", storageDir)
}

/**
 * Extension function to replace fragment in specified container view.
 */
internal fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    @IdRes containerViewId: Int = R.id.container_view
) {
    supportFragmentManager.beginTransaction().replace(containerViewId, fragment).commit()
}

/**
 * Extension function to add fragment in specified container view.
 */
internal fun AppCompatActivity.addFragment(
    fragment: Fragment,
    @IdRes containerViewId: Int = R.id.container_view
) {
    supportFragmentManager.beginTransaction().add(containerViewId, fragment).addToBackStack(null)
        .commit()
}

/**
 * Extension function to get color value from attribute
 */
internal fun Context.getColorAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data
}

/**
 * Extension function to get boolean value from attribute
 */
internal fun Context.getBooleanAttribute(@AttrRes attribute: Int): Boolean {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data == 0
}

/**
 * Extension function to get string value from attribute
 */
internal fun Context.getStringAttribute(@AttrRes attribute: Int): String {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.string.toString()
}

/**
 * Extension function to get integer value from attribute
 */
internal fun Context.getIntAttribute(@AttrRes attribute: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attribute, typedValue, true)
    return typedValue.data
}

/**
 * Extension function to register activity result intent
 */
internal fun ComponentActivity.registerActivityResult(
    name: String,
    errorCallback: (ActivityResult) -> Unit = {},
    successCallBack: (ActivityResult) -> Unit
): ActivityResultLauncher<Intent> {
    return activityResultRegistry.register(name, ActivityResultContracts.StartActivityForResult()) {
        it?.let { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                successCallBack(activityResult)
            } else {
                errorCallback(activityResult)
            }
        }
    }
}

/**
 * Extension function to get images from picker
 */
internal fun ActivityResult.getImages(pickerConfig: PickerConfig, callback: ImagePickerResultListener) {
    val isMultiPick = pickerConfig.allowMultipleSelection
    val pickerType = pickerConfig.pickerType
    if (isMultiPick) {
        val clipData = data?.clipData
        if (clipData != null) {
            val clipItemCount = clipData.itemCount
            val uriList = mutableListOf<Uri?>()
            for (i in 0 until clipItemCount) {
                val uri = clipData.getItemAt(i).uri
                uriList.add(uri)
            }
            callback.onMultiImagePick(uriList.filterNotNull(), pickerType)
        } else {
            val uri = data?.data
            uri?.let { callback.onImagePick(it, pickerType) }
        }
    } else {
        data?.data?.let { uri -> callback.onImagePick(uri, pickerType) }
    }
}

/**
 * Extension function to get parcelable from intent according to API level
 */
@Suppress("DEPRECATION")
internal inline fun <reified T : Parcelable> Intent.getModel(name: String = EXTRA_IMAGE_PICKER_CONFIG): T? {
    return if (isAtLeast13()) {
        getParcelableExtra(name, T::class.java)
    } else {
        getParcelableExtra(name)
    }
}

/**
 * Extension function to get parcelable from bundle according to API level
 */
@Suppress("DEPRECATION")
internal inline fun <reified T : Parcelable> Bundle.getModel(name: String = EXTRA_IMAGE_PICKER_CONFIG): T? {
    return if (isAtLeast13()) {
        getParcelable(name, T::class.java)
    } else {
        getParcelable(name)
    }
}

/**
 * Extension function to fetch images from media store.
 * Images are fetched according to passed selection and selection arguments.
 * The default sort order for fetched images is the date they were added and it is descending.
 */
internal suspend fun Context.getImagesList(
    selection: String? = null,
    selectionArgs: Array<String>? = null
): List<Image> {
    val imageList = mutableListOf<Image>()
    val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.BUCKET_ID,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Images.Media.SIZE
    )
    val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"
    val query = contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)
    return withContext(context = Dispatchers.IO) {
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val sizeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val bucketId = cursor.getLong(bucketIdColumn)
                val bucketName = cursor.getString(bucketNameColumn)
                val size = cursor.getLong(sizeColumn)
                val contentUri: Uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val folderName = if (bucketName.isNullOrEmptyOrBlank()) {
                    getFolderName(contentUri)
                } else {
                    bucketName
                }
                imageList += Image(id, contentUri, name, bucketId, folderName, size)
            }
        }
        imageList
    }
}

/**
 * Extension function to convert Megabyte to Byte
 */
internal fun Float.toByteSize(): Long {
    return try {
        val twoDecimalFloatingPoint = "%.2f".format(this).toFloat()
        (twoDecimalFloatingPoint * MB_TO_BYTE_MULTIPLIER).toLong()
    } catch (e: Exception) {
        e.printStackTrace()
        (this.toInt() * MB_TO_BYTE_MULTIPLIER).toLong()
    }
}

/**
 * Extension function to show toast from the fragment
 */
internal fun Fragment.toast(string: String) {
    Toast.makeText(requireContext(), string, Toast.LENGTH_LONG).show()
}

/**
 * Extension function to get uri from file path
 */
internal fun Context.getFileUri(filePath: String): Uri? {
    return FileProvider.getUriForFile(
        this,
        "${applicationContext.packageName}.${BuildConfig.LIBRARY_PACKAGE_NAME}.provider",
        File(filePath)
    )
}

/**
 * Gets the folder name from the uri.
 * Folder name is fetched by the file path.
 */
internal fun Context.getFolderName(contentURI: Uri): String {
    val path = getRealPathFromURI(contentURI)
    if (path.isNullOrEmptyOrBlank()) {
        return ""
    }
    val name = File(path).parentFile?.name
    if (!name.isNullOrEmptyOrBlank()) {
        return name
    }
    return ""
}

/**
 * Gets the real path from the URI.
 */
internal fun Context.getRealPathFromURI(contentURI: Uri): String? {
    val cursor: Cursor? = contentResolver.query(contentURI, null, null, null, null)
    val path = cursor?.use {
        it.moveToFirst()
        val index: Int = it.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        it.getString(index)
    }
    return path
}

/**
 * Internal function to check if string is null or empty or blank
 */
@OptIn(ExperimentalContracts::class)
internal fun String?.isNullOrEmptyOrBlank(): Boolean {
    contract {
        returns(false) implies (this@isNullOrEmptyOrBlank != null)
    }
    return this.isNullOrEmpty() || this.isNullOrBlank()
}