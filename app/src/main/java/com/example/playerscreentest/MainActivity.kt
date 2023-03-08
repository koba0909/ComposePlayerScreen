package com.example.playerscreentest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playerscreentest.comtainer.ContainerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv, ContainerFragment())
            .commit()
    }
}
