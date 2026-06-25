package com.example.screentool

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout

class MainActivity : Activity() { // שימוש ב-Activity המובנה של המערכת

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // יצירת העיצוב בקוד ללא צורך בקבצי XML
        val mainLayout = RelativeLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }

        val buttonParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            addRule(RelativeLayout.CENTER_IN_PARENT)
        }

        val btnToggleService = Button(this).apply {
            text = "הפעל התראת צילום"
            layoutParams = buttonParams
        }

        btnToggleService.setOnClickListener {
            val intent = Intent(this, NotificationService::class.java)
            startService(intent)
            finish()
        }

        mainLayout.addView(btnToggleService)
        setContentView(mainLayout)
    }
}
