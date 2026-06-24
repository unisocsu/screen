package com.example.screentool

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
import androidx.core.app.NotificationCompat
import java.io.File

class NotificationService : Service() {

    private val CHANNEL_ID = "screen_tool_channel"
    private val NOTIFICATION_ID = 9999

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // בודק אם הלחיצה הגיעה מההתראה ב-System UI
        if (intent?.action == "TAKE_SCREENSHOT") {
            takeScreenshotKitKat()
        } else {
            // אם השירות רק הופעל, מציגים את ההתראה
            showNotification()
        }
        return START_STICKY
    }

    private fun showNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // תמיכה בערוצי התראות (עבור מכשירים חדשים יותר), אנדרואיד 4.4 יתעלם מזה בבטחה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Screen Capture", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        // יצירת Intent שיקרא לשירות הזה שוב עם פקודת צילום
        val intent = Intent(this, NotificationService::class.java).apply {
            action = "TAKE_SCREENSHOT"
        }
        
        // PendingIntent תואם לאנדרואיד 4.4 (בלי ה-Flags המודרניים שמכשילים מכשירים ישנים)
        val pendingIntent = PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        // בניית ההתראה שתופיע ב-System UI
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("כלי צילום מסך")
            .setContentText("לחץ כאן כדי לצלם את המסך הנוכחי")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // מונע מהמשתמש למחוק את ההתראה בהחלקה
            .build()

        // הפיכת השירות ל-Foreground כדי שלא ייסגר ברקע
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun takeScreenshotKitKat() {
        // הגדרת נתיב שמירה בזיכרון המכשיר (מתאים ל-4.4)
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!storageDir.exists()) storageDir.mkdirs()
        val file = File(storageDir, "screenshot_${System.currentTimeMillis()}.png")
        
        try {
            // באנדרואיד 4.4, הדרך היחידה לצלם מסך מרקע היא לבקש הרשאת מערכת עמוקה (su)
            // ולהריץ את פקודת ה-screencap הפנימית של הלינוקס במכשיר
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
            // הודעת שגיאה אם המכשיר (4.4) אינו פרוץ או שאין לו הרשאת Root לקוד
            Toast.makeText(this, "שגיאה: באנדרואיד 4.4 חובה מכשיר פרוץ (Root) לצילום מסך", Toast.LENGTH_LONG).show()
        }
    }
}
