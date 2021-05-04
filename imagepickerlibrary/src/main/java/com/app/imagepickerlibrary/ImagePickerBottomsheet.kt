package com.app.imagepickerlibrary

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.imagepickerlibrary.databinding.BottomsheetLayoutUploadImageOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.lang.IllegalStateException

class ImagePickerBottomsheet(@LayoutRes val layoutId: Int = R.layout.bottomsheet_layout_upload_image_options) : BottomSheetDialogFragment(), View.OnClickListener {

    private var mListener: ItemClickListener? = null
    lateinit var binding: BottomsheetLayoutUploadImageOptionsBinding

    override fun getTheme(): Int {
        return R.style.roundBaseBottomSheetDialog
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), this.theme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheetInternal = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
            BottomSheetBehavior.from(bottomSheetInternal).state = BottomSheetBehavior.STATE_EXPANDED
        }
        initializeDataBinding()
        return binding.root
    }

    private fun initializeDataBinding(): BottomsheetLayoutUploadImageOptionsBinding? {
        binding = DataBindingUtil.inflate<BottomsheetLayoutUploadImageOptionsBinding>(LayoutInflater.from(requireContext()), layoutId, null, false).apply {
            lifecycleOwner = this@ImagePickerBottomsheet
            clickHandler = this@ImagePickerBottomsheet
            executePendingBindings()
        }
        return binding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener?.doCustomisations(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentFragment?.let { fragment ->
            mListener = if(fragment is ItemClickListener) fragment else throw IllegalStateException(getString(R.string.error_invalid_context_message))
        } ?: kotlin.run {
            mListener = if(context is ItemClickListener) context else throw IllegalStateException(getString(R.string.error_invalid_context_message))
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewChooseCamera -> {
                mListener?.onItemClick(bottomSheetActionCamera)
                dismiss()
            }
            R.id.textViewChooseGallery -> {
                mListener?.onItemClick(bottomSheetActionGallary)
                dismiss()
            }
            R.id.textViewChooseCancel -> {
                dismiss()
                mListener?.onItemClick(bottomSheetActionCancel)
            }
        }
    }

    fun setButtonText(cameraButtonText: String = getString(R.string.str_camera), galleryButtonText: String = getString(R.string.str_gallery), cancelButtonText: String = getString(R.string.str_cancel)) {
        binding.apply {
            textViewChooseCamera.text = cameraButtonText
            textViewChooseGallery.text = galleryButtonText
            textViewChooseCancel.text = cancelButtonText
        }
    }

    fun setButtonColors(
            @ColorInt cameraButtonColor: Int = ContextCompat.getColor(requireContext(), R.color.black),
            @ColorInt galleryButtonColor: Int = ContextCompat.getColor(requireContext(), R.color.black),
            @ColorInt cancelButtonColor: Int = ContextCompat.getColor(requireContext(), R.color.black)
    ) {
        binding.apply {
            textViewChooseCamera.setTextColor(cameraButtonColor)
            textViewChooseGallery.setTextColor(galleryButtonColor)
            textViewChooseCancel.setTextColor(cancelButtonColor)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setTextAppearance(@StyleRes buttonStyle: Int = R.style.TextViewImageOption) {
        binding.apply {
            textViewChooseCamera.setTextAppearance(buttonStyle)
            textViewChooseGallery.setTextAppearance(buttonStyle)
            textViewChooseCancel.setTextAppearance(buttonStyle)
        }
    }

    fun setBottomSheetBackgroundStyle(@DrawableRes bottomSheetStyle: Int = R.drawable.drawable_bottom_sheet_dialog) {
        binding.root.setBackgroundResource(bottomSheetStyle)
    }

    interface ItemClickListener {
        fun onItemClick(item: String?)
        fun doCustomisations(fragment: ImagePickerBottomsheet) {}
    }
}