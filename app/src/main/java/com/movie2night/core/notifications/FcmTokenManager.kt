package com.movie2night.core.notifications

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.movie2night.data.local.datastore.AuthDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object FcmTokenManager {

    var pendingToken: String? = null

    /**
     * Chamado no login e quando o token FCM é renovado.
     * Envia o token para o backend para que o servidor possa
     * enviar notificações push para este dispositivo.
     */
    fun registerToken(context: Context) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            sendTokenToBackend(context, token)
        }
    }

    fun sendTokenToBackend(context: Context, fcmToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataStore = AuthDataStore(context)
                val jwtToken  = dataStore.getToken() ?: return@launch

                val body = """{"fcmToken":"$fcmToken"}"""
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/users/me/fcm-token")
                    .patch(body)
                    .addHeader("Authorization", "Bearer $jwtToken")
                    .build()

                OkHttpClient().newCall(request).execute().use { response ->
                    println("FCM token enviado: ${response.code}")
                }
            } catch (e: Exception) {
                println("Erro ao enviar FCM token: ${e.message}")
            }
        }
    }
}