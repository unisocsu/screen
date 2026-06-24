package com.example.screentool

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.widget.Toast

class FloatingBubbleService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Floating Tool Started!", Toast.LENGTH_SHORT).show()
        
        // כאן יבוא קוד הבועה הצפה והנראות שלה על המסך בהמשך
        
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this, "Floating Tool Stopped", Toast.LENGTH_SHORT).show()
    }
}
