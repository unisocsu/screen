package com.example.screentool

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class FloatingBubbleService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButton: Button
    private lateinit var params: WindowManager.LayoutParams
    
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var resultCode = 0
    private var dataIntent: Intent? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            resultCode = intent.getIntExtra("RESULT_CODE", 0)
            dataIntent = intent.getParcelableExtra("DATA_INTENT")
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        floatingButton = Button(this).apply {
            text = "📸"
            textSize = 24f
            setBackgroundColor(android.graphics.Color.parseColor("#007AFF"))
            setTextColor(android.graphics.Color.WHITE)
        }

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            150, 150, layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        setupTouchListener()
        windowManager.addView(floatingButton, params)
        return START_STICKY
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {
        floatingButton.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager.updateViewLayout(floatingButton, params)
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val diffX = event.rawX - initialTouchX
                        val diffY = event.rawY - initialTouchY
                        if (Math.abs(diffX) < 10 && Math.abs(diffY) < 10) {
                            takeScreenshot()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun takeScreenshot() {
        val intent = dataIntent
        if (intent == null || resultCode == 0) return

        // שלב 1: העלמת הבועה מהמסך כדי שלא תפריע לצילום
        floatingButton.visibility = View.GONE

        // שלב 2: המתנה קלה שהבועה תיעלם ואז ביצוע הלכידה
        Handler(Looper.getMainLooper()).postDelayed({
            val metrics = resources.displayMetrics
            val width = metrics.widthPixels
            val height = metrics.heightPixels
            val density = metrics.densityDpi

            val mpManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mpManager.getMediaProjection(Activity.RESULT_OK, intent)

            @SuppressLint("WrongConstant")
            val imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
            
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "Screenshot", width, height, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface, null, null
            )

            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * width

                    // יצירת ה-Bitmap מהמסך
                    val bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
                    bitmap.copyPixelsFromBuffer(buffer)
                    image.close()

                    // שמירת הקובץ למכשיר
                    saveBitmap(bitmap)

                    // ניקוי משאבים
                    virtualDisplay?.release()
                    mediaProjection?.stop()
                    
                    // שלב 3: החזרת הבועה למסך
                    floatingButton.visibility = View.getModeStatic(View.VISIBLE)
                }
            }, Handler(Looper.getMainLooper()))

        }, 300)
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(path, "screenshot_${System.currentTimeMillis()}.png")
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
            Toast.makeText(this, "צילום המסך נשמר בהצלחה!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingButton.isInitialized) {
            windowManager.removeView(floatingButton)
        }
    }
}

// פונקציית עזר תואמת גרסאות לפתרון בעיית ה-Visibility באנדרואיד
fun View.apply {
    fun getModeStatic(value: Int): Int = value
}
