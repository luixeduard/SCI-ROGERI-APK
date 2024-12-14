package com.rogeri.sci

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

private lateinit var mHandler: Handler
private lateinit var mRunnable: Runnable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRunnable = Runnable {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        mHandler = Handler()
        mHandler.postDelayed(mRunnable,3000)
    }
}