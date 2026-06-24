package com.example.screentool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val btn = Button(this).apply {
            text = "Start Floating Tool"
            setOnClickListener {
                startService(Intent(this@MainActivity, FloatingBubbleService::class.java))
            }
        }

        setContentView(btn)
    }
}