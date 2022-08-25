package com.app.imagepickerlibrary.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.imagepickerlibrary.getFileUri
import com.app.imagepickerlibrary.getImagesList
import com.app.imagepickerlibrary.model.Folder
import com.app.imagepickerlibrary.model.Image
import com.app.imagepickerlibrary.model.PickerConfig
import com.app.imagepickerlibrary.model.Result
import com.app.imagepickerlibrary.util.compress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ImagePickerViewModel to manage all the interaction with UI and MediaStore.
 */
internal class ImagePickerViewModel(application: Application) : AndroidViewModel(application) {
    private val _pickerConfig = MutableStateFlow(PickerConfig.defaultPicker())
    val pickerConfig = _pickerConfig.asStateFlow()
    private val _resultFlow = MutableStateFlow<Result<List<Image>>>(Result.Loading)
    val resultFlow = _resultFlow.asStateFlow()
    private val _selectedFolder = MutableSharedFlow<Folder>()
    val selectedFolder = _selectedFolder.asSharedFlow()
    private val _folderFlow = MutableStateFlow<List<Folder>>(listOf())
    val folderFlow = _folderFlow.asStateFlow()
    private val _imageFlow = MutableStateFlow<List<Image>>(listOf())
    val imageFlow = _imageFlow.asStateFlow()
    private val _completeSelection = MutableSharedFlow<Boolean>()
    val completeSelection = _completeSelection.asSharedFlow()
    private val _selectedImages = mutableListOf<Image>()
    private val _updateImageCount = MutableSharedFlow<Boolean>()
    val updateImageCount = _updateImageCount.asSharedFlow()
    private val _disableInteraction = MutableSharedFlow<Boolean>()
    val disableInteraction = _disableInteraction.asSharedFlow()

    fun updatePickerConfig(pickerConfig: PickerConfig) {
        _pickerConfig.update { pickerConfig }
    }

    fun fetchImagesFromMediaStore() {
        _resultFlow.update { Result.Loading }
        viewModelScope.launch {
            val imageList = fetchImageList()
            _resultFlow.update { Result.Success(imageList) }
        }
    }

    private suspend fun fetchImageList(): List<Image> {
        val config = pickerConfig.value
        val (selection, selectionArgs) = config.generateSelectionArguments()
        val context = (getApplication() as Application).applicationContext
        return context.getImagesList(selection, selectionArgs)
    }

    fun getFoldersFromImages(images: List<Image>) {
        viewModelScope.launch(Dispatchers.IO) {
            val folders = images
                .groupBy { it.bucketId }
                .filter { it.value.isNotEmpty() }
                .map {
                    val image = it.value.first()
                    Folder(it.key, image.bucketName, image.uri, it.value)
                }
                .sortedBy { it.bucketName }
            _folderFlow.update { folders }
        }
    }

    fun getImagesFromFolder(bucketId: Long?, images: List<Image>) {
        if (bucketId == null) {
            _imageFlow.update { images }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val filteredImages = images.filter { it.bucketId == bucketId }
                _imageFlow.update { filteredImages }
            }
        }
    }

    fun openFolder(folder: Folder) {
        viewModelScope.launch {
            _selectedFolder.emit(folder)
        }
    }

    fun handleSelection(image: Image, selected: Boolean) {
        viewModelScope.launch {
            val isImageAdded = _selectedImages.any { image.id == it.id }
            if (selected) {
                if (!isImageAdded) {
                    _selectedImages.add(image)
                }
            } else {
                val index = _selectedImages.indexOfFirst { image.id == it.id }
                if (isImageAdded && index != -1) {
                    _selectedImages.removeAt(index)
                }
            }
            _updateImageCount.emit(true)
        }
    }

    fun handleDoneSelection() {
        viewModelScope.launch {
            _completeSelection.emit(true)
        }
    }

    fun getSelectedImages(): List<Image> {
        return _selectedImages
    }

    fun isImageSelected(image: Image): Boolean {
        return _selectedImages.any { image.id == it.id }
    }

    suspend fun compressImage(list: List<Image>): List<Image> {
        val compressedImageList = mutableListOf<Image>()
        val context = (getApplication() as Application).applicationContext
        val compressQuality = pickerConfig.value.compressQuality
        return withContext(Dispatchers.IO) {
            _disableInteraction.emit(true)
            list.forEach { image ->
                val compressImagePath = compress(context, image.uri, image.name, compressQuality)
                if (compressImagePath.isNullOrEmpty() || compressImagePath.isBlank()) {
                    compressedImageList.add(image.copy())
                } else {
                    val fileUri = context.getFileUri(compressImagePath)
                    fileUri?.let { compressedImageList.add(image.copy(uri = it)) }
                        ?: compressedImageList.add(image.copy())
                }
            }
            _disableInteraction.emit(false)
            compressedImageList
        }
    }
}