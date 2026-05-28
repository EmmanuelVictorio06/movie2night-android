package com.movie2night.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.movie2night.MainActivity

class MovieFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID   = "movie2night_matches"
        const val CHANNEL_NAME = "Matches e Convites"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title   = remoteMessage.notification?.title ?: "Movie2Night"
        val body    = remoteMessage.notification?.body  ?: "Você tem uma novidade!"
        val screen  = remoteMessage.data["screen"]  ?: "matches"
        val matchId = remoteMessage.data["matchId"] ?: ""

        showNotification(title, body, screen, matchId)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Salva o novo token — o FcmTokenManager vai enviar ao backend
        FcmTokenManager.pendingToken = token
        FcmTokenManager.sendTokenToBackend(applicationContext, token)
    }

    private fun showNotification(
        title: String,
        body: String,
        screen: String,
        matchId: String
    ) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificações de match e convites do Movie2Night"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent que abre o app na tela correta ao tocar na notificação
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("screen", screen)
            putExtra("matchId", matchId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}