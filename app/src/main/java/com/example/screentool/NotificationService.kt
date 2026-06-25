package com.example.screentool

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.widget.Toast
import java.io.File

class NotificationService : Service() {

    private val CHANNEL_ID = "screen_tool_channel"
    private val NOTIFICATION_ID = 9999

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "TAKE_SCREENSHOT") {
            takeScreenshotKitKat()
        } else {
            showNotification()
        }
        return START_STICKY
    }

    private fun showNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Screen Capture", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, NotificationService::class.java).apply {
            action = "TAKE_SCREENSHOT"
        }
        
        val pendingFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getService(this, 0, intent, pendingFlags)

        // יצירת ההתראה באמצעות ה-Builder המובנה של אנדרואיד
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        val notification = builder
            .setContentTitle("כלי צילום מסך")
            .setContentText("לחץ כאן כדי לצלם את המסך הנוכחי")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun takeScreenshotKitKat() {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists()) storageDir.mkdirs()
        val file = File(storageDir, "screenshot_${System.currentTimeMillis()}.png")
        
        try {
            val process = Runtime.getRuntime().exec("su")
            val os = process.outputStream
            os.write("screencap -p ${file.absolutePath}\n".toByteArray())
            os.write("exit\n".toByteArray())
            os.flush()
            os.close()
            
            process.waitFor()
            Toast.makeText(this, "המסך צולם ונשמר בגלריה!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "שגיאה בצילום המסך", Toast.LENGTH_LONG).show()
        }
    }
}
