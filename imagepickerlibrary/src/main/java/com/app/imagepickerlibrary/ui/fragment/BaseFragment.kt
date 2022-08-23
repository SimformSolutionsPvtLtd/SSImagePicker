package com.app.imagepickerlibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.app.imagepickerlibrary.listener.ItemClickListener
import com.app.imagepickerlibrary.model.Image
import com.app.imagepickerlibrary.model.Result
import com.app.imagepickerlibrary.viewmodel.ImagePickerViewModel
import kotlinx.coroutines.launch

/**
 * Base fragment for all fragments.
 */
internal abstract class BaseFragment<Binding : ViewDataBinding, T> : Fragment(),
    ItemClickListener<T> {
    protected lateinit var binding: Binding
    protected val viewModel: ImagePickerViewModel by activityViewModels {
        ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application
        )
    }
    private var disableInteraction = false

    /**
     * Get the layout resource ID for the screen.
     */
    @LayoutRes
    abstract fun getLayoutResId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.resultFlow.collect {
                        handleResult(it)
                    }
                }
                launch {
                    viewModel.disableInteraction.collect {
                        disableInteraction = it
                    }
                }
            }
        }
        onViewCreated()
    }

    abstract fun onViewCreated()

    private fun handleResult(result: Result<List<Image>>) {
        when (result) {
            Result.Loading -> {
                handleLoading(true)
            }
            is Result.Error -> {
                handleLoading(false)
                handleError(result.exception)
            }
            is Result.Success -> {
                handleLoading(false)
                handleSuccess(result.data)
            }
        }
    }

    open fun handleLoading(visible: Boolean) {}
    open fun handleError(exception: Exception) {}
    open fun handleSuccess(images: List<Image>) {}

    /**
     * If the disableInteraction is set to true all the click events are ignored otherwise
     * they are passed to child fragment.
     */
    override fun onItemClick(item: T, position: Int, @IdRes viewId: Int) {
        if (disableInteraction) {
            return
        }
        handleItemClick(item, position, viewId)
    }

    abstract fun handleItemClick(item: T, position: Int, viewId: Int)
}
