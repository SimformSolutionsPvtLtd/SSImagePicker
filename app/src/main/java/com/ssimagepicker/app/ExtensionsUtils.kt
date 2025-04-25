package com.ssimagepicker.app

import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.imagepickerlibrary.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * Extension function to load image into image view with Glide.
 */
fun AppCompatImageView.loadImage(
    url: Any?,
    isCircle: Boolean = false,
    isRoundedCorners: Boolean = false,
    func: RequestOptions.() -> Unit = {}
) {
    url?.let { image ->
        val options = RequestOptions().placeholder(R.mipmap.ic_launcher_round)
            .error(R.mipmap.ic_launcher_round)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(func)
        var requestBuilder = Glide.with(context).load(image).apply(options)
        if (isCircle) {
            requestBuilder = requestBuilder.apply(options.circleCrop())
        } else if (isRoundedCorners) {
            requestBuilder =
                requestBuilder.apply(options.transform(CenterCrop(), RoundedCorners(18)))
        }
        requestBuilder.into(this)
    }
}

//If you are using custom theming and need to change the status bar color,
// it may not work unless you specify a particular view object, like a toolbar.
fun AppCompatActivity.enableEdgeToEdge(view: View?) {
    view?.let {
        ViewCompat.setOnApplyWindowInsetsListener(it) { view, windowInsets ->
            val systemBarInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBarInsets.left,
                systemBarInsets.top,
                systemBarInsets.right,
                0
            )
            windowInsets
        }
    }
}

/**
 * Function to check if the system is at least android 11+
 */
fun isAtLeast11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R