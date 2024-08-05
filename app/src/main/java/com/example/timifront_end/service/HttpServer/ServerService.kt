package com.example.timifront_end.service.HttpServer

import android.Manifest
import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.timifront_end.R
import com.example.timifront_end.data.source.local.SaveTicket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val SERVER_PORT = 8080
val saveTicket: SaveTicket = SaveTicket()

data class ImageData(val image: String, val email: String)

class ServerService : Service() {
    override fun onCreate() {
        super.onCreate()
        startServer()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startForeground()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val name = "HttpServer"
        val descriptionText = "Notifications related to Timi http server"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("HttpServer", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, "HttpServer")
            .setContentTitle("Application is running")
            .setContentText("HTTP Server is running on port $SERVER_PORT")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSmallIcon(R.drawable.img)
            .build()
    }

    private fun startForeground() {
        createNotificationChannel()
        val internetPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        if (internetPermission == PackageManager.PERMISSION_DENIED) {
            stopSelf()
            return
        }
        try {
            val notification = createNotification()
            ServiceCompat.startForeground(
                this, 100, notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                } else {
                    0
                },
            )
        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is ForegroundServiceStartNotAllowedException
            ) {
                Log.e("ForegroundService", "Foreground service not allowed")
            }
        }
    }

    private fun startServer() {
        Log.d("Server", "Created server on port $SERVER_PORT")
        val scope = CoroutineScope(Dispatchers.IO)
        val context = this

        scope.launch {
            createServer(context = context)
        }
    }
}
