package com.wl.wldownload

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wl.download.toast
import com.wl.wldownload.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.tvDown.setOnClickListener{
            toast(this)
        }
    }
}