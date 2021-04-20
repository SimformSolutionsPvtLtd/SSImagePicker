# 📸SSImagePicker 

[![Android-Studio](https://img.shields.io/badge/Android%20Studio-4.0+-orange.svg?style=flat)](https://developer.android.com/studio/)
[![API](https://img.shields.io/badge/API-19%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=19)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)

Easy to use and configurable library to **Pick an image from the Gallery or Capture image using Camera**.

* You can easily select image from camera and gallery and upload it wherever you want. We have created this library to simplify pick or capture image feature.
* Handled permissions for camera and gallery, also supports scoped storage.
* Returns contentUri of selected image.
* Easy to use and supports all major devices.

# Features :

* Capture Image Using Camera
* Pick Image From Gallery
* Handle Runtime Permission For Storage And Camera
* ImagePicker Bottomsheet 
* Retrieve Image Result In Uri Format
* Crop Image
* Rotate Image
* Image Zoom In, Zoom Out
* Customize Image Picker BottomSheet Options Like :
     - Customize only text of buttons
     - Customize only text color of buttons
     - Customize multiple values of buttons like:
          - Text color, size, font family, padding using your own styles.xml
     - Customize bottomsheet's background shape and color


# 🎬Preview

| Capture Image Using Camera | Pick Image From Gallery | Customize Bottomsheet |
|--|--|--|
| ![](camera.gif) | ![](gallery.gif) | ![](bottomsheetCustomization.gif) |

| Crop Image | Rotate Image | Image Zoom in, Zoom out |
|--|--|--|
| ![](crop.gif) | ![](rotate.gif) | ![](zoomInzoomOut.gif)

# How it works:

1. Gradle Dependency

- Add the JitPack repository to your project's build.gradle file

```groovy
    allprojects {
    		repositories {
    			...
    			maven { url 'https://jitpack.io' }
    		}
    	}
```
- Add plugin in your app's build.gradle file

```groovy
plugins {
    ...
    id 'kotlin-kapt'
} 
```
- Add buildFeature in your app's build.gradle file

```groovy
android {
    ...
    buildFeatures {
        dataBinding = true
    }
}
```

- Add the dependency in your app's build.gradle file

```groovy
    dependencies {
    	        implementation 'com.github.SimformSolutionsPvtLtd:SSImagePicker:1.4'
    	}
```
2. Use ImagePicker Bottomsheet To Choose Option For Pick Image From Gallery Or Camera

```kotlin
    val fragment = ImagePickerBottomsheet()
    fragment.show(FragmentManager, String) 
```
3. Call ImagePickerActivityClass in your onCreate() To Handle Camera, Gallery Click And Permission Result. Pass Context, Activity And Request Permission Result Callback:

```kotlin
    var imagePicker = ImagePickerActivityClass(context,activity,onResult_Callback)
```

4. To Enable All Features(crop,rotate,zoomIn,zoomOut) call cropOptions(isAllCropFeaturesRequired: Boolean) And Pass true. By Default It's Set To False And Provides Only Crop Feature.

```kotlin
    override fun onCreate(savedInstanceState: Bundle?) {
            ...
            imagePicker.cropOptions(true)
        }
```

5. Allow Camera And Storage Permission To Pick Image And Send Your onRequestPermissionsResult To ImagePickerActivity

```kotlin
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) 
    {
         imagePicker.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
```
6. To Capture Image From Camera Use takePhotoFromCamera()

```kotlin
    imagePicker.takePhotoFromCamera()
```
7. To Pick Image From Gallery Use choosePhotoFromGallary()

```kotlin
    imagePicker.choosePhotoFromGallary()
```
8. Send Your onActivityResult to ImagePickerActivity

```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        imagePicker.onActivityResult(requestCode, resultCode, data)
    }
```
9. You Will Get Image Result In Uri Format In returnString() And Customize It To Upload 

```kotlin
     override fun returnString(item: Uri?) {
            **Here You Will Get Your Image Result In Uri Format**
        }
```
10. You can load image in your imageview using loadImage() func. (If you want to apply circleCrop() then pass isCircle = true, by default it's false)

```kotlin
     override fun returnString(item: Uri?) {
             imageViewEditProfile.loadImage(item, isCircle = true) {}
         }
```

# To customize bottomsheet:
* To customize bottomsheet, first override below method in your activity.
```kotlin
     override fun doCustomisations(fragment: ImagePickerBottomsheet) {
         //Do customizations here...
     }
```
* To customize text of buttons in Bottomsheet.
```kotlin
     fragment.setButtonText("Select Camera","Select Gallery","Remove")
```
* To only change text color of buttons in Bottomsheet.
```kotlin
     fragment.setButtonColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))
```
* To customize multiple values of buttons (Text color, size, font family, padding), you need to create a style in your style.xml.
```kotlin
     fragment.setTextAppearance(R.style.fontForNotificationLandingPage)
```
In styles.xml (Note: parent must be "Widget.AppCompat.TextView")
```xml
     <style name="fontForNotificationLandingPage" parent="Widget.AppCompat.TextView">
         <item name="android:fontFamily">@font/poppins_medium</item>
         <item name="android:textColor">@color/white</item>
         <item name="android:textSize">@dimen/_18ssp</item>
     </style>
```
Note: if setTextAppearance and setButtonColors both are used than whichever function is last called will override other one.
* To change bottomsheet's background (shape, color).
```kotlin
     fragment.setBottomSheetBackgroundStyle(R.drawable.drawable_bottom_sheet_dialog)
```
You need to make one drawable file of type shape.
```xml
     <shape xmlns:android="http://schemas.android.com/apk/res/android"
         android:shape="rectangle">
         <corners
             android:topLeftRadius="@dimen/_25sdp"
             android:topRightRadius="@dimen/_25sdp" />
         <padding android:top="@dimen/_5sdp" />
         <solid android:color="@color/colorPrimary" />
     </shape>
```
# Other Library used:
* __[UCrop Library](https://github.com/Yalantis/uCrop)__

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/SimformSolutionsPvtLtd/SSImagePicker/stargazers)__ for this repository. :star:

## License

```
Copyright 2020 Simform Solutions

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and limitations under the License.
```
