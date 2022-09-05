package com.ssimagepicker.app.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import com.app.imagepickerlibrary.model.PickExtension
import com.app.imagepickerlibrary.model.PickerType
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ssimagepicker.app.PickerOptions
import com.ssimagepicker.app.R
import com.ssimagepicker.app.databinding.BottomSheetPickerOptionsBinding

/**
 * PickerOptionsBottomSheet to display picker option related to new Image Picker.
 */
class PickerOptionsBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        const val BOTTOM_SHEET_TAG = "PICKER_BOTTOM_SHEET_TAG"
        const val PICKER_OPTIONS = "PICKER_OPTIONS"

        fun newInstance(pickerOptions: PickerOptions): PickerOptionsBottomSheet {
            val bundle = bundleOf(PICKER_OPTIONS to pickerOptions)
            val pickerOptionsBottomSheet = PickerOptionsBottomSheet()
            pickerOptionsBottomSheet.arguments = bundle
            return pickerOptionsBottomSheet
        }
    }

    private var mListener: PickerOptionsListener? = null
    private lateinit var binding: BottomSheetPickerOptionsBinding

    override fun getTheme(): Int {
        return R.style.PickerOptionsBottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), this.theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            BottomSheetBehavior.from(bottomSheetInternal).state =
                BottomSheetBehavior.STATE_HALF_EXPANDED
        }
        binding = BottomSheetPickerOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUI()
    }

    /**
     * Setting previously selected options with picker option object
     */
    private fun setUI() {
        binding.apply {
            clickHandler = this@PickerOptionsBottomSheet
            val pickerOptions = arguments?.getParcelable(PICKER_OPTIONS) ?: PickerOptions.default()

            val checkedPickerTypeId = if (pickerOptions.pickerType == PickerType.CAMERA) {
                R.id.camera_button
            } else {
                R.id.gallery_button
            }
            pickerTypeGroup.check(checkedPickerTypeId)

            multiSelectionSwitch.isChecked = pickerOptions.allowMultipleSelection
            countToolbarSwitch.isChecked = pickerOptions.showCountInToolBar
            folderSwitch.isChecked = pickerOptions.showFolders
            cameraInGallery.isChecked = pickerOptions.showCameraIconInGallery
            doneSwitch.isChecked = pickerOptions.isDoneIcon
            openCropSwitch.isChecked = pickerOptions.openCropOptions
            systemPickerSwitch.isChecked = pickerOptions.openSystemPicker
            compressImageSwitch.isChecked = pickerOptions.compressImage

            val checkedExtensionId = when (pickerOptions.pickExtension) {
                PickExtension.PNG -> R.id.png_button
                PickExtension.JPEG -> R.id.jpeg_button
                PickExtension.WEBP -> R.id.webp_button
                PickExtension.ALL -> R.id.all_button
            }
            extensionTypeGroup.check(checkedExtensionId)

            pickCountSlider.valueFrom = 1f
            pickCountSlider.valueTo = 15f
            pickCountSlider.stepSize = 1f
            pickCountSlider.value = pickerOptions.maxPickCount.toFloat()

            if (pickerOptions.maxPickSizeMB != Float.MAX_VALUE) {
                pickSizeTie.setText(pickerOptions.maxPickSizeMB.toString())
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.done_text -> {
                if (isDataValid()) {
                    openImagePicker()
                }
            }
        }
    }

    private fun openImagePicker() {
        val countValue = binding.pickCountSlider.value.toInt()
        val sizeValue = binding.pickSizeTie.text.toString().toFloat()
        val pickExtension = when (binding.extensionTypeGroup.checkedButtonId) {
            R.id.all_button -> PickExtension.ALL
            R.id.png_button -> PickExtension.PNG
            R.id.jpeg_button -> PickExtension.JPEG
            R.id.webp_button -> PickExtension.WEBP
            else -> PickExtension.ALL
        }
        val pickerType =
            if (binding.pickerTypeGroup.checkedButtonId == R.id.gallery_button) {
                PickerType.GALLERY
            } else {
                PickerType.CAMERA
            }
        val pickerOptions = PickerOptions(
            pickerType = pickerType,
            showCountInToolBar = binding.countToolbarSwitch.isChecked,
            showFolders = binding.folderSwitch.isChecked,
            allowMultipleSelection = binding.multiSelectionSwitch.isChecked,
            maxPickCount = countValue,
            maxPickSizeMB = sizeValue,
            pickExtension = pickExtension,
            showCameraIconInGallery = binding.cameraInGallery.isChecked,
            isDoneIcon = binding.doneSwitch.isChecked,
            openCropOptions = binding.openCropSwitch.isChecked,
            openSystemPicker = binding.systemPickerSwitch.isChecked,
            compressImage = binding.compressImageSwitch.isChecked
        )
        dismiss()
        mListener?.onPickerOptions(pickerOptions)
    }

    /**
     * Check whether the max pick count and max size is valid or not.
     * If all data is valid it will return true.
     */
    private fun isDataValid(): Boolean {
        val sizeValue = binding.pickSizeTie.text.toString().toFloatOrNull()
        if (sizeValue == null || sizeValue <= 0) {
            Toast.makeText(requireContext(), R.string.error_size_value, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    fun setClickListener(listener: PickerOptionsListener) {
        this.mListener = listener
    }

    interface PickerOptionsListener {
        fun onPickerOptions(pickerOptions: PickerOptions)
    }
}