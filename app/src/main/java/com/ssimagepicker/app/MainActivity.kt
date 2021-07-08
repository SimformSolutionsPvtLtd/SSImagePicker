package com.ssimagepicker.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.imagepickerlibrary.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    ImagePickerBottomsheet.ItemClickListener, ImagePickerActivityClass.OnResult {

    private lateinit var imagePicker: ImagePickerActivityClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imagePicker = ImagePickerActivityClass(this, this, activityResultRegistry, activity = this)
        //set to true if you want all features(crop,rotate,zoomIn,zoomOut)
        //by Default it's value is set to false (only crop feature is enabled)
        imagePicker.cropOptions(false)
        //set free crop where you can select image by picking squares.
        imagePicker.setFreeCrop(true)
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
                imagePicker.choosePhotoFromGallery()
            }
        }
    }

    //Override this method for customization of bottomsheet
    override fun doCustomisations(fragment: ImagePickerBottomsheet) {
        fragment.apply {
            //Customize button text
            setButtonText(
                cameraButtonText = "Select Camera",
                galleryButtonText = "Select Gallery",
                cancelButtonText = "Cancel"
            )

            //Customize button text color
            setButtonColors(
                galleryButtonColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

            //For more customization make a style in your styles xml and pass it to this method. (This will override above method result).
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                setTextAppearance(R.style.fontForNotificationLandingPage)
            }

            //To customize bottomsheet style
            setBottomSheetBackgroundStyle(R.drawable.drawable_bottom_sheet_dialog)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imagePicker.onActivityResult(requestCode, resultCode, data)
    }

    override fun returnString(item: Uri?) {
        imageViewEditProfile.loadImage(item, isCircle = true) {}
    }

}