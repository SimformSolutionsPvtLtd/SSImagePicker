# Picker Options Bottom Sheet UI Customization :
* To customize Picker Option Bottom Sheet UI, override the default bottom sheet theme **`SSImagePickerBaseBottomSheetDialog`** in your styles.xml or themes.xml. Make sure that the parent theme is set to **`SSImagePickerBaseBottomSheetDialog`**.

  ```xml
    <style name="CustomPickerBottomSheet" parent="SSImagePickerBaseBottomSheetDialog">
        ...      
    </style>
  ```
* Override one or more bottom sheet picker option ui attributes from the available [picker option ui attributes table](#picker-option-bottom-sheet-ui-attributes-table). For example,
  ```xml
    <style name="CustomPickerBottomSheet" parent="SSImagePickerBaseBottomSheetDialog">
        <item name="ssSheetCameraViewStyle">@style/PickerBottomSheetText</item>
        <item name="ssSheetGalleryViewStyle">@style/PickerBottomSheetText</item>
        <item name="ssSheetCancelViewStyle">@style/PickerBottomSheetText</item>
    </style>

    <!-- If you only want to change some values for a particular attribute it is better to add default style of that attribute as parent style.
         Here SSBottomSheetTextViewStyle is default style for picker bottom sheet's text button.
         You can avoid including parent style also and completely re-write your own definition of picker bottom sheet's text button-->
    <style name="PickerBottomSheetText" parent="SSBottomSheetTextViewStyle">
        <item name="android:fontFamily">@font/poppins_medium</item>
        <item name="android:textColor">@color/color_text</item>
        <item name="android:textSize">@dimen/_15ssp</item>
    </style>

    <!-- Without parent style. This is also valid. It will remove all the parent values for that style. -->
    <style name="PickerBottomSheetText">
        <item name="android:textColor">@color/black</item>
        <item name="android:textSize">@dimen/_13ssp</item>
        <item name="android:fontFamily">@font/poppins_medium</item>
    </style>
  ```
* Pass the custom style while creating the bottom sheet instance.
  ```kotlin
    val fragment = SSPickerOptionsBottomSheet.newInstance(R.style.CustomPickerBottomSheet)
    fragment.show(supportFragmentManager,SSPickerOptionsBottomSheet.BOTTOM_SHEET_TAG)
  ```

## Picker Option Bottom Sheet UI Attributes Table

| UI attribute | Format | Default value/style | Description |
|---|---|---|---|
| **ssSheetCameraText** | string | `"Camera"` | Text for the camera button. |
| **ssSheetGalleryText** | string | `"Gallery"` | Text for the gallery button. |
| **ssSheetCancelText** | string | `"Cancel"` | Text for the cancel button. |
| **ssSheetCameraButtonBackground** | reference/color | @drawable/bg_ss_picker_option_button | Background for the camera button. |
| **ssSheetGalleryButtonBackground** | reference/color | @drawable/bg_ss_picker_option_button | Background for the gallery button. |
| **ssSheetCancelButtonBackground** | reference/color | @drawable/bg_ss_picker_option_button_cancel | Background for the cancel button. |
| **ssSheetCameraViewStyle** | reference | SSBottomSheetTextViewStyle | Text view style for the camera button. |
| **ssSheetGalleryViewStyle** | reference | SSBottomSheetTextViewStyle | Text view style for the gallery button. |
| **ssSheetCancelViewStyle** | reference | SSBottomSheetTextViewStyle | Text view style for the cancel button. |
| **ssSheetBackground** | reference/color | @drawable/bg_ss_picker_option | Background for entire bottom sheet. |
