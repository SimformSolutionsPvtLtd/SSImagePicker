package com.app.imagepickerlibrary.ui.fragment

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.imagepickerlibrary.R
import com.app.imagepickerlibrary.databinding.FragmentImageBinding
import com.app.imagepickerlibrary.getStringAttribute
import com.app.imagepickerlibrary.model.Image
import com.app.imagepickerlibrary.model.PickerConfig
import com.app.imagepickerlibrary.model.Result
import com.app.imagepickerlibrary.toast
import com.app.imagepickerlibrary.ui.adapter.ImageAdapter
import com.app.imagepickerlibrary.ui.dialog.FullScreenImageDialogFragment
import kotlinx.coroutines.launch

/**
 * ImageFragment to display list of images.
 */
internal class ImageFragment : BaseFragment<FragmentImageBinding, Image>() {
    companion object {
        const val BUCKET_ID = "bucketId"
        fun newInstance(bucketId: Long): ImageFragment {
            return ImageFragment().apply {
                arguments = bundleOf(BUCKET_ID to bucketId)
            }
        }

        fun newInstance(): ImageFragment {
            return ImageFragment()
        }
    }

    private val imageAdapter = ImageAdapter(this)
    private var bucketId: Long? = null
    private var pickerConfig = PickerConfig.defaultPicker()
    private var maxPickError: String = ""

    override fun getLayoutResId(): Int = R.layout.fragment_image

    override fun onViewCreated() {
        bucketId = arguments?.getLong(BUCKET_ID)
        setRecyclerView(binding.rvImage)
        binding.rvImage.apply {
            adapter = imageAdapter
            setHasFixedSize(true)
        }
        maxPickError = requireContext().getStringAttribute(R.attr.ssImagePickerLimitText)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.imageFlow.collect {
                        updateImageList(it)
                    }
                }
                launch {
                    viewModel.pickerConfig.collect {
                        pickerConfig = it
                    }
                }
            }
        }
    }

    private fun updateImageList(imageList: List<Image>) {
        if (viewModel.resultFlow.value is Result.Loading) {
            return
        }
        val isImageListEmpty = imageList.isEmpty()
        binding.rvImage.isVisible = !isImageListEmpty
        binding.textNoData.isVisible = isImageListEmpty
        imageAdapter.setItemList(imageList)
    }

    override fun handleSuccess(images: List<Image>) {
        viewModel.getImagesFromFolder(bucketId, images)
    }

    override fun handleLoading(visible: Boolean) {
        binding.progressIndicator.isVisible = visible
    }

    override fun handleError(exception: Exception) {
        binding.rvImage.isVisible = false
        binding.textNoData.isVisible = true
    }

    override fun handleItemClick(item: Image, position: Int, viewId: Int) {
        if (viewId == R.id.root_item_image) {
            manageSelection(item, position)
        } else if (viewId == R.id.image_zoom) {
            showZoomImage(item)
        }
    }

    /**
     * The selection is done based on two configs.
     * If multiple selection is allowed then it checks whether the image is previously selected or not.
     * If the image is previously not selected then it will select the image and add it to the selected image list.
     * If the image is previously selected then it will un-select the image and remove it from the selected image list.
     * If multiple selection is not allowed then it adds the image to selected image list and marks the done via viewmodel.
     */
    private fun manageSelection(item: Image, position: Int) {
        if (pickerConfig.allowMultipleSelection) {
            if (viewModel.isImageSelected(item)) {
                selectImage(item, position, false)
            } else if (pickerConfig.maxPickCount > viewModel.getSelectedImages().size) {
                selectImage(item, position, true)
            } else {
                toast(maxPickError)
            }
        } else {
            viewModel.handleSelection(item, true)
            viewModel.handleDoneSelection()
        }
    }

    private fun selectImage(item: Image, position: Int, isSelected: Boolean) {
        item.isSelected = isSelected
        viewModel.handleSelection(item, isSelected)
        imageAdapter.notifyItemChanged(position)
    }

    private fun showZoomImage(item: Image) {
        val imageDialogFragment = FullScreenImageDialogFragment.newInstance(item)
        imageDialogFragment.show(childFragmentManager, FullScreenImageDialogFragment.FRAGMENT_TAG)
    }

    override fun onConfigurationChange() {
        setRecyclerView(binding.rvImage)
    }
}