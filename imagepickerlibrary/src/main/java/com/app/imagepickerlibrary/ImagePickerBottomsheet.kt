package com.app.imagepickerlibrary

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import com.app.imagepickerlibrary.databinding.BottomsheetLayoutUploadImageOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImagePickerBottomsheet(@LayoutRes val layoutId: Int = R.layout.bottomsheet_layout_upload_image_options) :
    BottomSheetDialogFragment(), View.OnClickListener {

    private var mListener: ItemClickListener? = null

    override fun getTheme(): Int {
        return R.style.RoundBottomSheetDialogThemeWhite
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
        return initializeDataBinding()?.root
    }

    private fun initializeDataBinding(): BottomsheetLayoutUploadImageOptionsBinding? {
        return DataBindingUtil.inflate<BottomsheetLayoutUploadImageOptionsBinding>(LayoutInflater.from(requireContext()), layoutId, null, false).apply {
            lifecycleOwner = this@ImagePickerBottomsheet
            clickHandler = this@ImagePickerBottomsheet
            executePendingBindings()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is ItemClickListener) {
            context
        } else {
            throw RuntimeException(
                "$context"
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

    interface ItemClickListener {
        fun onItemClick(item: String?)
    }
}