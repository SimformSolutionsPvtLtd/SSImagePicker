# Picker UI Customization
* To customize ImagePickerActivity UI, override the default **`SSImagePicker`** theme in your styles.xml or themes.xml. Make sure that the parent theme is set to **`SSImagePicker`**.

  ```xml
    <style name="CustomSSImagePicker" parent="SSImagePicker">
        ...      
    </style>
  ```
* Override one or more ui attributes for the picker screen from the available [ui attributes table](#picker-screen-ui-attributes-table). For example,
  ```xml
    <style name="CustomSSImagePicker" parent="SSImagePicker">
        <item name="ssToolbarBackIcon">@drawable/ic_arrow_back_ios</item>
        <item name="ssPickerGridCount">3</item>
        <item name="ssToolbarDoneTextAppearance">@style/CustomDoneTextAppearance</item>
    </style>

    <!-- If you only want to change some values for a particular attribute it is better to add default style of that attribute as parent style.
         Here SSToolbarDoneTextAppearance is default style for toolbar's done text. 
         You can avoid including parent style also and completely re-write your own definition of toolbar's done text's text appearance-->
    <style name="CustomDoneTextAppearance" parent="SSToolbarDoneTextAppearance">
        <item name="android:textColor">@color/black</item>
        <item name="android:textSize">@dimen/_13ssp</item>
        <item name="android:fontFamily">@font/poppins_medium</item>
    </style>

    <!-- Without parent style. This is also valid. It will remove all the parent values for that style. -->
    <style name="CustomDoneTextAppearance">
        <item name="android:textColor">@color/black</item>
        <item name="android:textSize">@dimen/_13ssp</item>
        <item name="android:fontFamily">@font/poppins_medium</item>
    </style>
  ```
* Set your custom theme into AndroidManifest.xml where you have defined the ImagePickerActivity
  ```xml
    <activity
        android:name="com.app.imagepickerlibrary.ui.activity.ImagePickerActivity"
        android:configChanges="orientation|screenSize"
        android:theme="@style/CustomSSImagePicker" />
  ```

## Picker Screen UI Attributes Table

| UI attribute | Format | Default value/style | Description |
|---|---|---|---|
| **ssStatusBarColor** | color | `#2D647D` | Status bar color |
| **ssStatusBarLightMode** | boolean | `true` | Used to determine status bar is in light or dark mode  |
| **ssToolbarBackground** | reference/color | `#2D647D` | Toolbar background |
| **ssToolbarBackIcon** | reference | @drawable/ic_ss_arrow_back | Toolbar back button drawable image |
| **ssToolbarTextAppearance** | reference | SSToolbarTitleTextAppearance | Toolbar title text appearance |
| **ssToolbarCameraIcon** | reference | @drawable/ic_ss_camera | Toolbar camera button drawable image. You can hide the icon via **cameraIcon(false)** in picker configuration. |
| **ssToolbarDoneIcon** | reference | @drawable/ic_ss_done | Toolbar done button drawable image. By default the icon will be displayed for done. |
| **ssToolbarDoneText** | string | `"Done"` | Toolbar done button text. You can show the text via **doneIcon(false)** in picker configuration |
| **ssToolbarDoneTextAppearance** | reference | SSToolbarDoneTextAppearance | Toolbar done button text appearance. |
| **ssPickerBackground** | reference/color  | `#FFFFFF` | Picker screen surface background. |
| **ssProgressIndicatorStyle** | reference | SSProgressIndicatorStyle | Style for the circular progress indicator which is displayed while the images are being fetched. |
| **ssFolderTextViewStyle** | reference | SSFolderTextStyle | Text view style for the folder name which appears on the folder. |
| **ssNoDataText** | string | `"No data found"` | Text which is showed to user when there is no image in a folder or no images are found. |
| **ssNoDataTextViewStyle** | reference | SSNoDataTextStyle | Text view style for the no data text view. |
| **ssImagePickerLimitText** | string | `"Maximum selection reached"` | The text which will be displayed to user when the user tries to select more images then the specified pick limit. This message will be displayed as **Toast**. |
| **ssPickerGridCount** | integer | `2` | Grid count for the picker screen in **Portrait** mode. It will be same for both folder and image. |
| **ssPickerGridCountLandscape** | integer | `4` | Grid count for the picker screen in **Landscape** mode. It will be same for both folder and image. |
| **ssImageSelectIcon** | reference | @drawable/ic_ss_check_circle | Drawable icon which is used to indicate that the image is selected. It is displayed on top of selected image in multiple selection mode. |
| **ssImageZoomIcon** | reference | @drawable/ic_ss_zoom_eye | Drawable icon which is used to open image in dialog mode to see the image in full view mode. |
| **ssUCropToolbarColor** | color | Picker toolbar background (ssToolbarBackground) | UCrop activity toolbar color. The default value is ssToolbarBackground so that both can be same. You can override the UCrop value by specifying your color. |
| **ssUCropStatusBarColor** | color | Picker status bar color (ssStatusBarColor) | UCrop activity status bar color. The default value is ssStatusBarColor so that both can be same. You can override the UCrop value by specifying your color. |
| **ssUCropToolbarWidgetColor** | color | `#FFFFFF` | UCrop activity toolbar widget color. |
| **ssUCropActiveControlWidgetColor** | color | `#2D647D` | UCrop activity active control widget color. |