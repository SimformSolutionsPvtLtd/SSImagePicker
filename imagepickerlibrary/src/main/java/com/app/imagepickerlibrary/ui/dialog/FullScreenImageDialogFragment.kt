package com.app.imagepickerlibrary.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import coil.load
import com.app.imagepickerlibrary.R
import com.app.imagepickerlibrary.databinding.DialogFragmentFullScreenImageBinding
import com.app.imagepickerlibrary.model.Image

/**
 * FullScreenImageDialogFragment to display image full screen with transparent background.
 */
internal class FullScreenImageDialogFragment : DialogFragment() {
    companion object {
        const val IMAGE = "image"
        const val FRAGMENT_TAG = "FullScreenImage"
        fun newInstance(image: Image): FullScreenImageDialogFragment {
            return FullScreenImageDialogFragment().apply {
                arguments = bundleOf(IMAGE to image)
            }
        }
    }

    private lateinit var binding: DialogFragmentFullScreenImageBinding

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = resources.getDimensionPixelSize(R.dimen._350sdp)
            it.window?.setLayout(width, height)
            it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentFullScreenImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image: Image? = arguments?.getParcelable(IMAGE)
        if (image == null) {
            dismiss()
        }
        binding.imageView.load(image?.uri)
    }
}