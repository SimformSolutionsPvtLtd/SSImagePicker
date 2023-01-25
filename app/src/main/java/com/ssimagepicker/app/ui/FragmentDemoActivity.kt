package com.ssimagepicker.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ssimagepicker.app.R
import com.ssimagepicker.app.databinding.ActivityFragmentDemoBinding

class FragmentDemoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFragmentDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_fragment_demo)
        title = getString(R.string.fragment_demo)
        supportFragmentManager.beginTransaction().add(R.id.frame_layout, DemoFragment())
            .commit()
    }
}
