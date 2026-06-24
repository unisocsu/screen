package com.example.screentool

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.ImageView

class FloatingBubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var bubble: View

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val icon = ImageView(this).apply {
            setImageResource(android.R.drawable.presence_video_online)

            setOnClickListener {
                startService(Intent(this@FloatingBubbleService, ScreenService::class.java).apply {
                    putExtra("mode", "shot")
                })
            }

            setOnLongClickListener {
                startService(Intent(this@FloatingBubbleService, ScreenService::class.java).apply {
                    putExtra("mode", "record")
                })
                true
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.x = 100
        params.y = 300

        windowManager.addView(icon, params)
        bubble = icon
    }

    override fun onDestroy() {
        windowManager.removeView(bubble)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}