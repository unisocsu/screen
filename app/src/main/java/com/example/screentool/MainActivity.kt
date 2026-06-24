package com.example.screentool

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.screentool.R // שורה קריטית! פותרת את שגיאת ה-Build ב-GitHub

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnToggleService = findViewById<Button>(R.id.btnToggleService)
        btnToggleService.text = "הפעל התראת צילום"

        btnToggleService.setOnClickListener {
            // מפעיל את שירות ההתראות
            val intent = Intent(this, NotificationService::class.java)
            startService(intent)
            finish() // סוגר את המסך כדי שהמשתמש יוכל לצלם מסכים אחרים
        }
    }
}
