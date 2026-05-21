package com.movie2night.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

// Extensão que cria o DataStore uma única vez para o Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthDataStore(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data
            .map { it[TOKEN_KEY] }
            .firstOrNull()
    }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { it[USER_ID_KEY] = userId }
    }

    suspend fun getUserId(): String? {
        return context.dataStore.data
            .map { it[USER_ID_KEY] }
            .firstOrNull()
    }

    // Chamado no logout — apaga tudo
    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    fun isLoggedIn() = context.dataStore.data.map { it[TOKEN_KEY] != null }
}
