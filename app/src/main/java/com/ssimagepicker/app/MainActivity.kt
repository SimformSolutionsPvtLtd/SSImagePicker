package com.ssimagepicker.app

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.app.imagepickerlibrary.ImagePickerActivityClass
import com.app.imagepickerlibrary.ImagePickerBottomsheet
import com.app.imagepickerlibrary.bottomSheetActionCamera
import com.app.imagepickerlibrary.bottomSheetActionFragment
import com.app.imagepickerlibrary.bottomSheetActionGallary
import com.app.imagepickerlibrary.loadImage
import kotlinx.android.synthetic.main.activity_main.imageViewEditProfile

class MainActivity : AppCompatActivity(), View.OnClickListener, ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.onResult {

    private lateinit var imagePicker: ImagePickerActivityClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imagePicker = ImagePickerActivityClass(this, this, this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageViewEditProfile, R.id.edit_logo -> {
                val fragment = ImagePickerBottomsheet()
                fragment.show(supportFragmentManager, bottomSheetActionFragment)
            }
        }
    }

    override fun onItemClick(item: String?) {
        when {
            item.toString() == bottomSheetActionCamera -> {
                imagePicker.takePhotoFromCamera()
            }
            item.toString() == bottomSheetActionGallary -> {
                imagePicker.choosePhotoFromGallary()
            }
        }
    }

    //Override this method for customization of bottomsheet
    override fun doCustomisations(fragment: ImagePickerBottomsheet) {
        fragment.apply {
            //Customize button text
            setButtonText(cameraButtonText = "Select Camera", galleryButtonText = "Select Gallery", cancelButtonText = "Cancel")

            //Customize button text color
            setButtonColors(galleryButtonColor = ContextCompat.getColor(requireContext(), R.color.white))

            //For more customization make a style in your styles xml and pass it to this method. (This will override above method result).
            setTextAppearance(R.style.fontForNotificationLandingPage)

            //To customize bottomsheet style
            setBottomSheetBackgroundStyle(R.drawable.drawable_bottom_sheet_dialog)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        imagePicker.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        imagePicker.onActivityResult(requestCode, resultCode, data)
    }

    override fun returnString(item: Uri?) {
        imageViewEditProfile.loadImage(item, isCircle = true) {}
    }

}