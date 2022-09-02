# Migration

- [**1.8**  :arrow_right:  **2.0**](#from-18-to-20)

## From 1.8 to 2.0
1. Remove all the reference of the **ImagePickerActivityClass**. Follow the [How It Works](../README.md#books-how-it-works) section for initialization.
2. After doing that just open the image picker activity from your desired location via registering for ImagePicker object. The open method takes the type of picker which which will open the desired picker type.
    ```kotlin
    private val imagePicker: ImagePicker = registerImagePicker(callback = this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
    }
    
    //To display the picker screen call open method on image picker object passing the picker type.
    imagePicker.open(PickerType.GALLERY)
    ```
3. You can add customization like multiple selection, max image size via the available methods in the **`ImagePicker`** class.
    ```kotlin
    imagePicker
            .title("My Picker")
            .multipleSelection(enable = true, maxCount = 5)
            .showCountInToolBar(false)
            .showFolder(true)
            .cameraIcon(true)
            .doneIcon(true)
            .allowCropping(true)
            .compressImage(false)
            .maxImageSize(2)
            .extension(PickExtension.JPEG)
    imagePicker.open(PickerType.GALLERY)
    ```
4. Get the result by implementing the **`ImagePickerResultListener`**.
    ```kotlin
    override fun onImagePick(uri: Uri?) {
        //For single selection or image captured with camera.
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
        //For multiple image selection from gallery.
    }
    ```
5. The **`ImagePickerBottomsheet`** is renamed to **`SSPickerOptionsBottomSheet`**. You can open the bottom sheet like this.
   ```kotlin
    val pickerOptionBottomSheet = SSPickerOptionsBottomSheet.newInstance()
    pickerOptionBottomSheet.show(supportFragmentManager,"tag")
   ```
6. To get bottom sheet selection result override **`SSPickerOptionsBottomSheet.ImagePickerClickListener`** in your activity or fragment. You can receive the selected provider type in the **`onImageProvider`** method.
   ```kotlin
    class MainActivity : AppCompatActivity(), SSPickerOptionsBottomSheet.ImagePickerClickListener {

        override fun onImageProvider(provider: ImageProvider) {
            when (provider) {
                ImageProvider.GALLERY -> {
                    //Open gallery picker
                }
                ImageProvider.CAMERA -> {
                    //Open camera picker
                }
                ImageProvider.NONE -> {}
            }
        }

    }
   ```
7. The **`loadImage()`** extension function on the **`AppCompatImageView`** has been removed. If you are using the function you can add the function with below implementation. The **`loadImage()`** extension function used [Glide](https://github.com/bumptech/glide) internally to load the image.
   ```kotlin
    fun AppCompatImageView.loadImage(url: Any?, isCircle: Boolean = false, isRoundedCorners: Boolean = false, func: RequestOptions.() -> Unit) {
        url?.let { image ->
            val options = RequestOptions().placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(func)
            val requestBuilder = Glide.with(context).load(image).apply(options)
            if (isCircle) {
                requestBuilder.apply(options.circleCrop())
            } else if(isRoundedCorners){
                requestBuilder.apply(options.transforms(CenterCrop(), RoundedCorners(18)))
            }
            requestBuilder.into(this)
        }
    }
   ```