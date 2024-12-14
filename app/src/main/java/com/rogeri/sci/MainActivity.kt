package com.rogeri.sci

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

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