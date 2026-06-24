package com.example.screentool

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ScreenService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // כאן ירוץ בעתיד קוד צילום המסך ברקע
        return START_STICKY
    }
}
