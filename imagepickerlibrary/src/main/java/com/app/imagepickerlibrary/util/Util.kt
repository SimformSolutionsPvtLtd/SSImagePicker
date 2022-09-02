package com.app.imagepickerlibrary.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions.getExtensionVersion
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.exifinterface.media.ExifInterface
import com.app.imagepickerlibrary.createImageFile
import com.app.imagepickerlibrary.getRealPathFromURI
import com.app.imagepickerlibrary.isNullOrEmptyOrBlank
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Utility function to check that the system is running on at least android 13.
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
internal fun isAtLeast13(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}

/**
 * Utility function to check if system picker is available or not on Android 11+.
 * The function is provided by google to check whether the photo picker is available or not
 * [More Details](https://developer.android.com/training/data-storage/shared/photopicker#check-availability)
 *
 * Using SuppressLint to remove warning about the getExtensionVersion method.
 */
@SuppressLint("NewApi")
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
internal fun isPhotoPickerAvailable(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        true
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        getExtensionVersion(Build.VERSION_CODES.R) >= 2
    } else {
        false
    }
}

/**
 * Utility function to compress the image.
 * The function returns the file path of the compressed image.
 */
@Suppress("DEPRECATION")
internal fun compress(context: Context, uri: Uri, name: String, compressQuality: Int): String? {
    val filePath = context.getRealPathFromURI(uri)
    if (filePath.isNullOrEmptyOrBlank()) {
        return null
    }
    var scaledBitmap: Bitmap? = null
    val options = BitmapFactory.Options()
    var bmp: Bitmap = BitmapFactory.decodeFile(filePath, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth
    val maxHeight = actualHeight / 1.5
    val maxWidth = actualWidth / 1.5
    var imgRatio =
        if (actualWidth < actualHeight) (actualWidth / actualHeight).toFloat() else (actualHeight / actualWidth).toFloat()
    val maxRatio = maxWidth / maxHeight
    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        if (imgRatio < maxRatio) {
            imgRatio = (maxHeight / actualHeight).toFloat()
            actualWidth = (imgRatio * actualWidth).toInt()
            actualHeight = maxHeight.toInt()
        } else if (imgRatio > maxRatio) {
            imgRatio = (maxWidth / actualWidth).toFloat()
            actualHeight = (imgRatio * actualHeight).toInt()
            actualWidth = maxWidth.toInt()
        } else {
            actualHeight = maxHeight.toInt()
            actualWidth = maxWidth.toInt()
        }
    }
    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
    options.inJustDecodeBounds = false
    options.inPurgeable = true
    options.inInputShareable = true
    options.inTempStorage = ByteArray(16 * 1024)
    try {
        bmp = BitmapFactory.decodeFile(filePath, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    if (scaledBitmap == null) {
        return null
    }
    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )
    val exif: ExifInterface
    try {
        exif = ExifInterface(filePath)
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, 0
        )
        val matrix = Matrix()
        when (orientation) {
            6 -> {
                matrix.postRotate(90f)
            }
            3 -> {
                matrix.postRotate(180f)
            }
            8 -> {
                matrix.postRotate(270F)
            }
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap, 0, 0,
            scaledBitmap.width, scaledBitmap.height, matrix,
            true
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
    val out: FileOutputStream?
    val resultFilePath = context.createImageFile("COMPRESS_${name}").absolutePath
    try {
        out = FileOutputStream(resultFilePath)
        scaledBitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, out)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    return resultFilePath
}

/**
 * Utility function to calculate the InSample Size
 */
private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
        val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
        inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
    }
    val totalPixels = (width * height).toFloat()
    val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
        inSampleSize++
    }
    return inSampleSize
}