package com.example.screentool

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // יצירת כפתור תוכנתי ללא תלות בקובצי ה-XML של ה-res
        val btn = Button(this).apply {
            text = "Start Floating Tool"
            setOnClickListener {
                HandleServiceStart()
            }
        }

        setContentView(btn)
    }

    private fun HandleServiceStart() {
        // בדיקה: אם המכשיר הוא אנדרואיד 6.0 (API 23) ומעלה, יש לבקש הרשאת חלון צף בזמן ריצה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // פתיחת מסך הגדרות המערכת כדי שהמשתמש יאשר את הבועה הצפה
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1234)
                Toast.makeText(this, "Please enable overlay permission", Toast.LENGTH_LONG).show()
            } else {
                // ההרשאה כבר קיימת - מפעילים את השירות
                startFloatingService()
            }
        } else {
            // במכשיר האנדרואיד 4.4 (KitKat) שלך - אין צורך בבדיקה, מפעילים מיד!
            startFloatingService()
        }
    }

    private fun startFloatingService() {
        startService(Intent(this@MainActivity, FloatingBubbleService::class.java))
    }

    // תפיסת התשובה מההגדרות (עבור מכשירים מודרניים)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                startFloatingService()
            } else {
                Toast.makeText(this, "Permission denied. Cannot start tool.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
