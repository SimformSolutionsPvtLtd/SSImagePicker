package com.ssimagepicker.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ssimagepicker.app.R
import com.ssimagepicker.app.databinding.ActivityLaunchBinding
import com.ssimagepicker.app.enableEdgeToEdge

class LaunchActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_launch)
        binding.clickHandler = this
        setUpToolbar()
        enableEdgeToEdge(binding.toolbar.root)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.activity_button -> {
                goToScreen(MainActivity::class.java)
            }
            R.id.fragment_button -> {
                goToScreen(FragmentDemoActivity::class.java)
            }
        }
    }

    private fun <T> goToScreen(activity: Class<T>) {
        startActivity(Intent(this, activity))
    }

    private fun setUpToolbar() {
        binding.toolbar.apply {
            title = this@LaunchActivity.title.toString()
            imageBackButton.visibility = View.GONE
        }
    }
}
