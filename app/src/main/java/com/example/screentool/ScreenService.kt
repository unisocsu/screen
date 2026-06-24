package com.example.screentool

import android.app.*
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ScreenService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(1, notification())

        return START_NOT_STICKY
    }

    private fun notification(): Notification {
        val id = "screen"

        if (android.os.Build.VERSION.SDK_INT >= 26) {
            val ch = NotificationChannel(id, "screen", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(ch)
        }

        return NotificationCompat.Builder(this, id)
            .setContentTitle("ScreenTool running")
            .setSmallIcon(android.R.drawable.presence_video_online)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}