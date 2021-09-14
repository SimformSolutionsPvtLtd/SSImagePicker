package com.app.imagepickerlibrary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.bumptech.glide.load.resource.bitmap.CenterCrop




// Code for upload Profile Picture
fun checkPermissionForUploadImage(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
}

fun askPermissionForUploadImage(activity: Activity) {

    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CODE)
}

fun Activity.dispatchTakePictureIntent(onGetImageFromCameraActivityResult: ActivityResultLauncher<Intent>): Uri? {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        packageManager?.run {
            takePictureIntent.resolveActivity(this)?.also {
                // Create the File where the photo should go
                try {
                    val timeStamp: String = SimpleDateFormat(dateFormatForTakePicture, Locale.getDefault()).format(Date())
                    createImageFile(timeStamp).apply {
                        // Continue only if the File was successfully created
                        var photoURI: Uri? = null
                        also { photo ->
                            photoURI = FileProvider.getUriForFile(this@dispatchTakePictureIntent, "${applicationContext.packageName}.${BuildConfig.LIBRARY_PACKAGE_NAME}.provider", photo)
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            onGetImageFromCameraActivityResult.launch(takePictureIntent)
                        }
                        return photoURI
                    }
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    return null
                }
            }
        }
    }
    return null
}

fun Activity.createImageFile(name: String = ""): File {
    // Create an image file name
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${name}_", ".jpg", storageDir)
}

fun AppCompatImageView.loadImage(url: Any?, isCircle: Boolean = false, isRoundedCorners: Boolean = false, func: RequestOptions.() -> Unit) {
    url?.let { image ->
        val options = RequestOptions().placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(func)
        val requestBuilder = Glide.with(context).load(image).apply(options)
        if (isCircle) {
            requestBuilder.apply(options.circleCrop())
        } else if(isRoundedCorners){
            requestBuilder.apply(options.transforms(CenterCrop(), RoundedCorners(18)))
        }
        requestBuilder.into(this)
    }
}