package com.app.beloz.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }

        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_SURNAME = "user_surname"
        const val USER_EMAIL = "user_email"
        const val NUM_TELEFONO = "num_telefono"
        const val USER_TOKEN = "user_token"
    }

    fun saveSession(userId: Int, userName: String, userSurname: String, userEmail: String, numTelefono: String, userToken: String?) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, userId)
        editor.putString(USER_NAME, userName)
        editor.putString(USER_SURNAME, userSurname)
        editor.putString(USER_EMAIL, userEmail)
        editor.putString(NUM_TELEFONO, numTelefono)
        editor.putString(USER_TOKEN, userToken)
        editor.apply()
        println("Session guardada con token: $userToken")
        Log.d("SessionManager", "Phone number saved: $numTelefono")

    }

    fun getUserId(): Int {
        return prefs.getInt(USER_ID, -1)
    }

    fun getUserName(): String? {
        return prefs.getString(USER_NAME, null)
    }
    fun getUserSurname(): String? {                 // Añadido
        return prefs.getString(USER_SURNAME, null)
    }

    fun getUserEmail(): String? {
        return prefs.getString(USER_EMAIL, null)
    }
    fun getUserPhoneNumber(): String? {                // Añadido
        return prefs.getString(NUM_TELEFONO, null)
    }

    fun getUserToken(): String? {
        val token = prefs.getString(USER_TOKEN, null)
        println("Token obtenido de la sesión: $token")
        return token
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return getUserId() != -1
    }

}
