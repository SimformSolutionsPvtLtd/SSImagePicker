package com.ssimagepicker.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import java.io.IOException
import kotlinx.android.synthetic.main.activity_main.imageViewEditProfile

class MainActivity : AppCompatActivity(), View.OnClickListener, BottomSheetFragmentForUploadImageOptions.ItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageViewEditProfile, R.id.edit_logo -> {
                checkForPermission()
            }
        }
    }

    private fun checkForPermission() {
        if (checkPermissionForUploadImage(this)) {
            val fragment = BottomSheetFragmentForUploadImageOptions()
            fragment.show(supportFragmentManager, bottomSheetActionFragment)
        } else {
            askPermissionForUploadImage(this)
        }
    }

    override fun onItemClick(item: String?) {
        when {
            item.toString() == bottomSheetActionCamera -> {
                takePhotoFromCamera()
            }
            item.toString() == bottomSheetActionGallary -> {
                choosePhotoFromGallary()
            }
        }
    }

    private fun takePhotoFromCamera() {
        dispatchTakePictureIntent().apply {
            this?.let {
                imageFilePath = it
                imageViewEditProfile.loadImage(imageFilePath, isCircle = true) {}
            }
        }
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == REQUEST_GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    contentURI?.let {
                        imageFilePath = it.toString()
                        imageViewEditProfile.loadImage(imageFilePath, isCircle = true) {}
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

        }
    }

}