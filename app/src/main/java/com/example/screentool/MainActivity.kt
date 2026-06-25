package com.example.screentool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. יצירת לLayout ראשי בקוד (במקום קובץ activity_main.xml)
        val mainLayout = RelativeLayout(this)
        mainLayout.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )

        // 2. הגדרת מיקום הכפתור שיהיה בדיוק במרכז המסך
        val buttonParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }

        // 3. יצירת הכפתור עצמו והגדרת התוכן שלו
        val btnToggleService = Button(this).apply {
            text = "הפעל התראת צילום"
            layoutParams = buttonParams
        }

        // 4. הגדרת פעולת הלחיצה על הכפתור
        btnToggleService.setOnClickListener {
            val intent = Intent(this, NotificationService::class.java)
            startService(intent)
            finish() // סוגר את המסך כדי לאפשר צילום חופשי
        }

        // 5. הוספת הכפתור לתוך ה-Layout והצגתו על המסך
        mainLayout.addView(btnToggleService)
        setContentView(mainLayout)
    }
}
