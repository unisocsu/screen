package com.example.screentool

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FloatingBubbleService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // כאן ירוץ בעתיד הקוד שמציג את הבועה הצפה על המסך
        return START_STICKY
    }
}
