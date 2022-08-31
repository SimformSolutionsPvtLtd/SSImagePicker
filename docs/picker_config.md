# Picker Config Customization

- Add customization to image picker via the image picker object. The table below mentions all the methods with description and default values to add more customization. 
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
```

- Display the picker screen by calling open method and passing the picker type **`PickerType.GALLERY`** or **`PickerType.CAMERA`**
```kotlin
imagePicker.open(PickerType.GALLERY)
```

## Config Methods

| Method Name | Description | Default Value |
|---|---|---|
| title(title: String) | Change toolbar title of ImagePickerActivity | "Image Picker" |
| showCountInToolBar(enable: Boolean) | Displays selected image count in toolbar after title for multi selection mode | true |
| showFolder(show: Boolean) | Opens ImagePickerActivity in folder mode. | true |
| multipleSelection(enable: Boolean) | Enable multiple selection with maximum pick limit. The maximum limit for the picking image is **15**. | false (Disable multiple selection) , 15 (Max limit) |
| multipleSelection(enable: Boolean, maxCount: Int) | Enable multiple selection with a pick limit. The maximum limit for the picking image is **15**. | false |
| maxImageSize(maxSizeMB: Int) | Filter image based on size. Images smaller than this size will be displayed in image picker. | Float.MAX_VALUE |
| maxImageSize(maxSizeMB: Float) | Filter image based on size. Only two decimal points will be considered in float. Images smaller than this size will be displayed in image picker. | Float.MAX_VALUE |
| extension(pickExtension: PickExtension) | Filter image based on extension. Images whose extension matches the type will be displayed. | PickExtension.ALL |
| cameraIcon(enable: Boolean) | Displays camera icon in gallery picker mode. **If the image is captured via camera in gallery picker mode (single/multiple selection), it will give result in onImagePick(uri: Uri?)** callback. | true |
| doneIcon(enable: Boolean) | In multi selection mode the done view should be icon or text. If false is passed it will display the **Text**. If true is passed it will display the done as **Icon**. The done is only visible in **multiple selection** mode. | true **(Icon Displayed)** |
| allowCropping(enable: Boolean) | Open cropping screen with underlying **UCrop** library. **This method works in single selection gallery mode or if an image is taken via camera.** | false |
| systemPicker(enable: Boolean) | Opens new system photo picker for Android 11+. This method only works on **Android 11+**. Only **multi selection with pick limit** and **extension** will supported with system picker. All other parameters will be ignored. There is platform limit on maximum pick count ([More Details](https://developer.android.com/training/data-storage/shared/photopicker#select_multiple_media_items_2)). **SSImagePicker** handles this for you automatically. | false |
| compressImage(enable: Boolean, quality: Int = 75) | Enable compression of image or not. For single selection or the image captured via camera in any picker mode the compression is handled via **UCrop**. For multiple selection the height and width of images are divided by 1.5 to scale down the image and the quality is applied on that scaled down image. If the compression fails for any reason the original uri for that image is returned. | false |