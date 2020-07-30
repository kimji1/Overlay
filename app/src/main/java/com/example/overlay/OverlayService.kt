package com.example.overlay

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
import android.widget.Button
import android.widget.ProgressBar
import androidx.core.app.NotificationCompat


class OverlayService : Service() {

    private var wm: WindowManager? = null
    private var mView: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val strId = "notificationId"
            val strTitle = getString(R.string.app_name)
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var channel = notificationManager.getNotificationChannel(strId)
            if (channel == null) {
                channel = NotificationChannel(strId, strTitle, NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
            val notification: Notification = NotificationCompat.Builder(this, strId).build()
            startForeground(1, notification)
        }
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,  // Android O 이상인 경우 TYPE_APPLICATION_OVERLAY 로 설정
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL

        val inflate = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        mView = inflate.inflate(R.layout.overlay_view, null)
        mView?.findViewById<Button>(R.id.button)?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                startActivity(Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                stopSelf()
            }
            false
        }
        val progressBar = mView?.findViewById<ProgressBar>(R.id.progress_bar)
        startTimer(progressBar)

        wm?.addView(mView, params)
    }

    private fun startTimer(progressBar: ProgressBar?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true) // Foreground service 종료
        }
        if (mView != null) {
            wm?.removeView(mView) // View 초기화
            mView = null
        }
        wm = null
    }
}