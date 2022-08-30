package com.app.imagepickerlibrary.ui.fragment

import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.imagepickerlibrary.R
import com.app.imagepickerlibrary.databinding.FragmentFolderBinding
import com.app.imagepickerlibrary.model.Folder
import com.app.imagepickerlibrary.model.Image
import com.app.imagepickerlibrary.model.Result
import com.app.imagepickerlibrary.ui.adapter.FolderAdapter
import kotlinx.coroutines.launch

/**
 * FolderFragment to display list of folders.
 */
internal class FolderFragment : BaseFragment<FragmentFolderBinding, Folder>() {
    companion object {
        fun newInstance(): FolderFragment {
            return FolderFragment()
        }
    }

    private val folderAdapter = FolderAdapter(this)

    override fun getLayoutResId(): Int = R.layout.fragment_folder

    override fun onViewCreated() {
        setRecyclerView(binding.rvFolder)
        binding.rvFolder.adapter = folderAdapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.folderFlow.collect {
                        updateFolderList(it)
                    }
                }
            }
        }
    }

    private fun updateFolderList(folderList: List<Folder>) {
        if (viewModel.resultFlow.value is Result.Loading) {
            return
        }
        val isFolderListEmpty = folderList.isEmpty()
        binding.rvFolder.isVisible = !isFolderListEmpty
        binding.textNoData.isVisible = isFolderListEmpty
        folderAdapter.setItemList(folderList)
    }

    override fun handleSuccess(images: List<Image>) {
        viewModel.getFoldersFromImages(images)
    }

    override fun handleLoading(visible: Boolean) {
        binding.progressIndicator.isVisible = visible
    }

    override fun handleError(exception: Exception) {
        binding.rvFolder.isVisible = false
        binding.textNoData.isVisible = true
    }

    override fun handleItemClick(item: Folder, position: Int, viewId: Int) {
        viewModel.openFolder(item)
    }

    override fun onConfigurationChange() {
        setRecyclerView(binding.rvFolder)
    }
}