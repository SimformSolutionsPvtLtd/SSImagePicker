package com.app.imagepickerlibrary

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yalantis.ucrop.UCrop
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImagePickerActivityClass(private val context: Context, private val callback: OnResult, private val registry: ActivityResultRegistry, private val activity: Activity? = null, private val fragment: Fragment? = null) {

    private var functionSelection = FunctionProvider.NONE
    private var fileUri: Uri? = null
    private var isCropAllFeaturesRequired: Boolean? = false

    init {
        if(activity == null && fragment == null) {
            throw IllegalArgumentException(context.getString(R.string.error_exception_message))
        }
    }

    private fun checkForPermission(): Boolean {
        return checkPermissionForUploadImage(context)
    }

    fun takePhotoFromCamera() {
        functionSelection = FunctionProvider.CAMERA
        if (checkForPermission()) {
            fileUri = activity?.dispatchTakePictureIntent(onGetImageFromCameraActivityResult) ?: fragment?.activity?.dispatchTakePictureIntent(onGetImageFromCameraActivityResult)
        } else {
            askPermission()
        }
    }

    fun choosePhotoFromGallery() {
        functionSelection = FunctionProvider.GALLERY
        if (checkForPermission()) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            onGetImageFromGalleryActivityResult.launch(galleryIntent)
        } else {
            askPermission()
        }
    }

    @SuppressLint("MissingSuperCall")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when (functionSelection) {
                        FunctionProvider.CAMERA -> {
                            takePhotoFromCamera()
                        }
                        FunctionProvider.GALLERY -> {
                            choosePhotoFromGallery()
                        }
                        else -> {
                            //
                        }
                    }
                } else {
                    Toast.makeText(context, "Important Permission Required..", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun startCrop(imageUri: Uri?) {
        val imageFile = activity?.createImageFile(SimpleDateFormat(dateFormatForTakePicture, Locale.getDefault()).format(Date()))
                ?: fragment?.activity?.createImageFile(SimpleDateFormat(dateFormatForTakePicture, Locale.getDefault()).format(Date()))
        imageUri?.let {
            if (activity != null) {
                UCrop.of(imageUri, Uri.fromFile(imageFile)).withOptions(getUCropOptions()).start(activity)
            } else if (fragment != null) {
                UCrop.of(imageUri, Uri.fromFile(imageFile)).withOptions(getUCropOptions()).start(context, fragment)
            }
        }
    }

    fun cropOptions(isAllCropFeaturesRequired: Boolean) {
        isCropAllFeaturesRequired = isAllCropFeaturesRequired
    }

    /* Applies app colors to Crop activity UI. */
    private fun getUCropOptions(): UCrop.Options {
        return UCrop.Options().apply {
            if (isCropAllFeaturesRequired != true) {
                setHideBottomControls(true)
            }
            setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
            setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            setToolbarWidgetColor(ContextCompat.getColor(context, R.color.design_default_color_on_primary))
            setActiveControlsWidgetColor(ContextCompat.getColor(context, R.color.colorAccent))
        }
    }

    @SuppressLint("MissingSuperCall")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            callback.returnString(data?.let { intent -> UCrop.getOutput(intent) })
        }
    }

    interface OnResult {
        fun returnString(item: Uri?)
    }

    private val onGetImageFromGalleryActivityResult = registry.register("Gallery", ActivityResultContracts.StartActivityForResult()) { result ->
        result?.let { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                activityResult.data?.data?.let { uri ->
                    startCrop(uri)
                }
            }
        }
    }

    private val onGetImageFromCameraActivityResult = registry.register("Camera", ActivityResultContracts.StartActivityForResult()) { result ->
        result?.let { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                if (fileUri != null) {
                    startCrop(fileUri)
                }
            }
        }
    }

    private fun askPermission() {
        permissionResult.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA))
    }

    private val permissionResult = registry.register("permission", ActivityResultContracts.RequestMultiplePermissions()) { result ->
        result?.let { mutableMap ->
            if(mutableMap.entries.all { entry -> entry.value == true }) {
                when(functionSelection) {
                    FunctionProvider.CAMERA -> {
                        takePhotoFromCamera()
                    }
                    FunctionProvider.GALLERY -> {
                        choosePhotoFromGallery()
                    }
                    else -> {
                        //
                    }
                }
            }
        }
    }
}