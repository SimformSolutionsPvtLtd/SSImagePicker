package com.app.imagepickerlibrary.ui.bottomsheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleRes
import androidx.core.os.bundleOf
import com.app.imagepickerlibrary.R
import com.app.imagepickerlibrary.databinding.BottomSheetImagePickerOptionsBinding
import com.app.imagepickerlibrary.model.ImageProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * SSPickerOptionsBottomSheet to display picker option related to Image Picker.
 * It displays a bottom sheet with options Gallery, Camera and Cancel.
 * The bottom sheet can be modified via SSImagePickerBaseBottomSheetDialog theme
 */
class SSPickerOptionsBottomSheet : BottomSheetDialogFragment(), View.OnClickListener {
    companion object {
        const val BOTTOM_SHEET_TAG = "IMAGE_PICKER_BOTTOM_SHEET_TAG"
        const val THEME_ID = "theme_id"
        fun newInstance(@StyleRes themeId: Int): SSPickerOptionsBottomSheet {
            return SSPickerOptionsBottomSheet().apply {
                arguments = bundleOf(THEME_ID to themeId)
            }
        }
    }

    private var mListener: ImagePickerClickListener? = null
    private lateinit var binding: BottomSheetImagePickerOptionsBinding

    override fun getTheme(): Int {
        return R.style.SSImagePickerBaseBottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val themeId = arguments?.getInt(THEME_ID, -1) ?: 0
        if (themeId > 0) {
            return try {
                BottomSheetDialog(requireContext(), themeId)
            } catch (e: Exception) {
                e.printStackTrace()
                BottomSheetDialog(requireContext(), this.theme)
            }
        }
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
            BottomSheetBehavior.from(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding = BottomSheetImagePickerOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clickHandler = this
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentFragment?.let { fragment ->
            mListener =
                if (fragment is ImagePickerClickListener) fragment else throw IllegalStateException(
                    getString(
                        R.string.error_invalid_context_listener
                    )
                )
        } ?: kotlin.run {
            mListener =
                if (context is ImagePickerClickListener) context else throw IllegalStateException(
                    getString(
                        R.string.error_invalid_context_listener
                    )
                )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewChooseCamera -> {
                mListener?.onImageProvider(ImageProvider.CAMERA)
                dismiss()
            }
            R.id.textViewChooseGallery -> {
                mListener?.onImageProvider(ImageProvider.GALLERY)
                dismiss()
            }
            R.id.textViewChooseCancel -> {
                mListener?.onImageProvider(ImageProvider.NONE)
                dismiss()
            }
        }
    }

    interface ImagePickerClickListener {
        fun onImageProvider(provider: ImageProvider)
    }
}