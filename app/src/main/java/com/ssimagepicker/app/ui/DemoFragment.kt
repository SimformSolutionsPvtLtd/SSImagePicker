package com.ssimagepicker.app.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.ImageProvider
import com.app.imagepickerlibrary.model.PickerType
import com.app.imagepickerlibrary.ui.bottomsheet.SSPickerOptionsBottomSheet
import com.ssimagepicker.app.PickerOptions
import com.ssimagepicker.app.R
import com.ssimagepicker.app.databinding.FragmentDemoBinding
import com.ssimagepicker.app.isAtLeast11

class DemoFragment : Fragment(), View.OnClickListener,
    SSPickerOptionsBottomSheet.ImagePickerClickListener,
    ImagePickerResultListener, PickerOptionsBottomSheet.PickerOptionsListener {

    private lateinit var binding: FragmentDemoBinding

    companion object {
        private const val IMAGE_LIST = "IMAGE_LIST"
    }

    private val imagePicker: ImagePicker by lazy {
        registerImagePicker(this)
    }
    private val imageList = mutableListOf<Uri>()
    private val imageDataAdapter = ImageDataAdapter(imageList)
    private var pickerOptions = PickerOptions.default()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_demo, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
        setUI(savedInstanceState)
    }

    private fun setUI(savedInstanceState: Bundle?) {
        binding.imageRecyclerView.adapter = imageDataAdapter
        if (savedInstanceState != null && savedInstanceState.containsKey(IMAGE_LIST)) {
            val uriList: List<Uri> =
                savedInstanceState.getParcelableArrayList(IMAGE_LIST) ?: listOf()
            updateImageList(uriList)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.options_button -> {
                openPickerOptions()
            }
            R.id.open_picker_button -> {
                openImagePicker()
            }
            R.id.open_sheet_button -> {
                val fragment =
                    SSPickerOptionsBottomSheet.newInstance(R.style.CustomPickerBottomSheet)
                fragment.show(childFragmentManager, SSPickerOptionsBottomSheet.BOTTOM_SHEET_TAG)
            }
        }
    }

    /**
     * This method receives the selected picker type option from the bottom sheet.
     */
    override fun onImageProvider(provider: ImageProvider) {
        when (provider) {
            ImageProvider.GALLERY -> {
                pickerOptions = pickerOptions.copy(pickerType = PickerType.GALLERY)
                openImagePicker()
            }
            ImageProvider.CAMERA -> {
                pickerOptions = pickerOptions.copy(pickerType = PickerType.CAMERA)
                openImagePicker()
            }
            ImageProvider.NONE -> {
                //User has pressed cancel show anything or just leave it blank.
            }
        }
    }

    /**
     * Opens the options for picker. The picker option is bottom sheet with many input parameters.
     */
    private fun openPickerOptions() {
        val fragment = PickerOptionsBottomSheet.newInstance(pickerOptions)
        fragment.setClickListener(this)
        fragment.show(childFragmentManager, PickerOptionsBottomSheet.BOTTOM_SHEET_TAG)
    }

    /**
     * Once the picker options are selected in bottom sheet
     * we will receive the latest picker options in this method
     */
    override fun onPickerOptions(pickerOptions: PickerOptions) {
        this.pickerOptions = pickerOptions
        openImagePicker()
    }

    /**
     * Open the image picker according to picker type and the ui options.
     * The new system picker is only available for Android 13+.
     */
    private fun openImagePicker() {
        imagePicker
            .title("My Picker")
            .multipleSelection(pickerOptions.allowMultipleSelection, pickerOptions.maxPickCount)
            .showCountInToolBar(pickerOptions.showCountInToolBar)
            .showFolder(pickerOptions.showFolders)
            .cameraIcon(pickerOptions.showCameraIconInGallery)
            .doneIcon(pickerOptions.isDoneIcon)
            .allowCropping(pickerOptions.openCropOptions)
            .compressImage(pickerOptions.compressImage)
            .maxImageSize(pickerOptions.maxPickSizeMB)
            .extension(pickerOptions.pickExtension)
        if (isAtLeast11()) {
            imagePicker.systemPicker(pickerOptions.openSystemPicker)
        }
        imagePicker.open(pickerOptions.pickerType)
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
