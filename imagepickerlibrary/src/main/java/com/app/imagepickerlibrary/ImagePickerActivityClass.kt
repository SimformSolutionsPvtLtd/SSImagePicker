package com.app.imagepickerlibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat.startActivityForResult
import java.io.IOException

@Suppress("DEPRECATION")
class ImagePickerActivityClass(private val context: Context,private val activity: Activity, private val callback: onResult) {

    private var functionSelection = FunctionProvider.NONE
    private var fileUri: Uri? = null

    fun checkForPermission(): Boolean {
        return checkPermissionForUploadImage(context)
    }

    fun takePhotoFromCamera() {
        functionSelection = FunctionProvider.CAMERA
        if (checkForPermission()) {
            fileUri = activity.dispatchTakePictureIntent()
        }
        else {
            askPermissionForUploadImage(activity)
        }
    }

    fun choosePhotoFromGallary() {
        functionSelection = FunctionProvider.GALLERY
        if (checkForPermission()) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activity.startActivityForResult(galleryIntent, REQUEST_GALLERY)
        }
        else {
            askPermissionForUploadImage(activity)
        }
    }

    @SuppressLint("MissingSuperCall")
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when(functionSelection) {
                        FunctionProvider.CAMERA -> {
                            takePhotoFromCamera()
                        }
                        FunctionProvider.GALLERY -> {
                            choosePhotoFromGallary()
                        }
                        else -> {
                            //
                        }
                    }
                } else {
                    Toast.makeText(context, "Important Permission Required..", Toast.LENGTH_SHORT)
                            .show()
                }
                return
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                callback.returnString(data.data)
            }
        }
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (fileUri != null) {
                callback.returnString(fileUri)
            }
        }
    }

    interface onResult {
        fun returnString(item: Uri?)
    }

}