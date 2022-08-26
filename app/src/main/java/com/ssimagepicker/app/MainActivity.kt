package com.ssimagepicker.app

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.ImageProvider
import com.app.imagepickerlibrary.model.PickExtension
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.ssimagepicker.app.databinding.ActivityMainBinding

/**
 * MainActivity which displays all the functionality of the ImagePicker library. All the attributes are modified with the ui.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener,
    SSPickerOptionsBottomSheet.ImagePickerClickListener,
    ImagePickerResultListener {

    companion object {
        private const val IMAGE_LIST = "IMAGE_LIST"
    }

    private lateinit var binding: ActivityMainBinding
    private val imagePicker: ImagePicker = registerImagePicker(this@MainActivity)
    private val imageList = mutableListOf<Uri>()
    private val imageDataAdapter = ImageDataAdapter(imageList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.clickHandler = this
        setUI(savedInstanceState)
    }

    private fun setUI(savedInstanceState: Bundle?) {
        binding.imageRecyclerView.adapter = imageDataAdapter
        binding.pickCountSlider.valueFrom = 1f
        binding.pickCountSlider.valueTo = 15f
        binding.pickCountSlider.stepSize = 1f
        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_LIST)) {
            val uriList: List<Uri> =
                savedInstanceState.getParcelableArrayList(IMAGE_LIST) ?: listOf()
            updateImageList(uriList)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.open_picker_button -> {
                val pickerType =
                    if (binding.pickerTypeGroup.checkedButtonId == R.id.gallery_button) {
                        PickerType.GALLERY
                    } else {
                        PickerType.CAMERA
                    }
                checkForImagePicker(pickerType)
            }
            R.id.open_sheet_button -> {
                val fragment =
                    SSPickerOptionsBottomSheet.newInstance(R.style.CustomPickerBottomSheet)
                fragment.show(supportFragmentManager, SSPickerOptionsBottomSheet.BOTTOM_SHEET_TAG)
            }
        }
    }

    /**
     * This method receives the selected picker type option from the bottom sheet.
     */
    override fun onImageProvider(provider: ImageProvider) {
        when (provider) {
            ImageProvider.GALLERY -> {
                checkForImagePicker(PickerType.GALLERY)
            }
            ImageProvider.CAMERA -> {
                checkForImagePicker(PickerType.CAMERA)
            }
            ImageProvider.NONE -> {
                //User has pressed cancel show anything or just leave it blank.
            }
        }
    }

    private fun checkForImagePicker(pickerType: PickerType) {
        if (isDataValid()) {
            openImagePicker(pickerType)
        }
    }

    /**
     * Check whether the max pick count and max size is valid or not.
     * If all data is valid it will return true.
     */
    private fun isDataValid(): Boolean {
        val sizeValue = binding.pickSizeTie.text.toString().toFloatOrNull()
        if (sizeValue == null || sizeValue <= 0) {
            Toast.makeText(this, R.string.error_size_value, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /**
     * Open the image picker according to picker type and the ui options.
     * The new system picker is only available for Android 13+.
     */
    private fun openImagePicker(pickerType: PickerType) {
        val countValue = binding.pickCountSlider.value.toInt()
        val sizeValue = binding.pickSizeTie.text.toString().toFloat()
        val pickExtension = when (binding.extensionTypeGroup.checkedButtonId) {
            R.id.all_button -> PickExtension.ALL
            R.id.png_button -> PickExtension.PNG
            R.id.jpeg_button -> PickExtension.JPEG
            R.id.webp_button -> PickExtension.WEBP
            else -> PickExtension.ALL
        }
        imagePicker
            .title("My Picker")
            .multipleSelection(binding.multiSelectionSwitch.isChecked, countValue)
            .showCountInToolBar(binding.countToolbarSwitch.isChecked)
            .showFolder(binding.folderSwitch.isChecked)
            .cameraIcon(binding.cameraInGallery.isChecked)
            .doneIcon(binding.doneSwitch.isChecked)
            .allowCropping(binding.openCropSwitch.isChecked)
            .compressImage(binding.compressImageSwitch.isChecked)
            .maxImageSize(sizeValue)
            .extension(pickExtension)
        if (isAtLeast11()) {
            imagePicker.systemPicker(binding.systemPickerSwitch.isChecked)
        }
        imagePicker.open(pickerType)
    }

    /**
     * Single Selection and the image captured from camera will be received in this method.
     */
    override fun onImagePick(uri: Uri?) {
        uri?.let { updateImageList(listOf(it)) }
    }

    /**
     * Multiple Selection uris will be received in this method
     */
    override fun onMultiImagePick(uris: List<Uri>?) {
        if (!uris.isNullOrEmpty()) {
            updateImageList(uris)
        }
    }

    private fun updateImageList(list: List<Uri>) {
        imageList.clear()
        imageList.addAll(list)
        imageDataAdapter.notifyDataSetChanged()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(IMAGE_LIST, ArrayList(imageList))
        super.onSaveInstanceState(outState)
    }
}