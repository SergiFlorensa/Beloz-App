package com.app.beloz.utils

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SessionManager(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("user_session_secure", Context.MODE_PRIVATE)
    private val legacyPrefs = context.applicationContext.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private var cachedEnvelope: String? = null
    private var cachedSession: JSONObject? = null

    val sessionChanges: StateFlow<Long> = changes.asStateFlow()

    init {
        synchronized(lock) {
            // Commit the encrypted copy before removing an existing plaintext session.
            var migrationCommitted = prefs.contains(ENVELOPE)
            if (!migrationCommitted && legacyPrefs.contains(USER_ID)) {
                val legacy = JSONObject().apply {
                    put(USER_ID, legacyPrefs.getInt(USER_ID, -1))
                    listOf(USER_NAME, USER_SURNAME, USER_EMAIL, NUM_TELEFONO, USER_TOKEN).forEach {
                        put(it, legacyPrefs.getString(it, null))
                    }
                }
                if (isValid(legacy)) {
                    migrationCommitted = runCatching { persist(legacy) }.isSuccess
                } else {
                    legacyPrefs.edit().clear().commit()
                }
            }
            if (migrationCommitted) legacyPrefs.edit().clear().commit()
        }
    }

    companion object {
        @Volatile private var INSTANCE: SessionManager? = null
        private val lock = Any()
        private val changes = MutableStateFlow(0L)
        private const val ENVELOPE = "encrypted_session_v1"
        private const val KEY_ALIAS = "beloz_session_aes_gcm_v1"
        private val AAD = "beloz.session.v1".toByteArray(Charsets.UTF_8)

        fun getInstance(context: Context): SessionManager = INSTANCE ?: synchronized(lock) {
            INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
        }

        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_SURNAME = "user_surname"
        const val USER_EMAIL = "user_email"
        const val NUM_TELEFONO = "num_telefono"
        const val USER_TOKEN = "user_token"

        // This is only a local expiry guard. The server must verify the JWT signature.
        internal fun tokenExpiresAt(token: String?): Long? = runCatching {
            val parts = token?.split('.') ?: return null
            if (parts.size != 3 || parts.any { it.isBlank() }) return null
            val payload = JSONObject(String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_WRAP), Charsets.UTF_8))
            val expiry = (payload.opt("exp") as? Number)?.toLong() ?: return null
            expiry.takeIf { it > 0 }
        }.getOrNull()

        private fun isValid(session: JSONObject): Boolean =
            session.optInt(USER_ID, -1) > 0 &&
                (tokenExpiresAt(session.optString(USER_TOKEN)) ?: 0L) > System.currentTimeMillis() / 1000
    }

    fun saveSession(userId: Int, userName: String, userSurname: String, userEmail: String, numTelefono: String, userToken: String?) {
        val session = JSONObject().apply {
            put(USER_ID, userId)
            put(USER_NAME, userName)
            put(USER_SURNAME, userSurname)
            put(USER_EMAIL, userEmail)
            put(NUM_TELEFONO, numTelefono)
            put(USER_TOKEN, userToken)
        }
        check(isValid(session)) { "La sesion no es valida. Vuelve a iniciar sesion." }
        synchronized(lock) {
            try {
                persist(session)
                check(legacyPrefs.edit().clear().commit())
                changes.value += 1
            } catch (_: Exception) {
                clearSession()
                throw IllegalStateException("No se pudo guardar la sesion de forma segura.")
            }
        }
    }

    fun getUserId(): Int = readSession()?.optInt(USER_ID, -1) ?: -1
    fun getUserName(): String? = readSession()?.optString(USER_NAME)
    fun getUserSurname(): String? = readSession()?.optString(USER_SURNAME)
    fun getUserEmail(): String? = readSession()?.optString(USER_EMAIL)
    fun getUserPhoneNumber(): String? = readSession()?.optString(NUM_TELEFONO)
    fun getUserToken(): String? = readSession()?.optString(USER_TOKEN)
    fun isLoggedIn(): Boolean = readSession() != null
    fun getTokenExpiryEpochSeconds(): Long? = tokenExpiresAt(getUserToken())

    fun clearSession() = synchronized(lock) {
        cachedEnvelope = null
        cachedSession = null
        prefs.edit().clear().commit()
        legacyPrefs.edit().clear().commit()
        // Removing the key also makes any stale encrypted disk copy unusable.
        runCatching { keyStore().deleteEntry(KEY_ALIAS) }
        changes.value += 1
        Unit
    }

    fun clearSessionIfTokenMatches(token: String) = synchronized(lock) {
        if (getUserToken() == token) clearSession()
    }

    private fun readSession(): JSONObject? = synchronized(lock) {
        val envelope = prefs.getString(ENVELOPE, null) ?: return@synchronized null
        try {
            val session = if (cachedEnvelope == envelope) cachedSession else decrypt(envelope)
            if (session == null || !isValid(session)) {
                clearSession()
                null
            } else {
                cachedEnvelope = envelope
                cachedSession = session
                session
            }
        } catch (_: Exception) {
            // A restored, corrupted or invalidated key must never fall back to plaintext.
            clearSession()
            null
        }
    }

    private fun persist(session: JSONObject) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey())
        cipher.updateAAD(AAD)
        val envelope = JSONObject().apply {
            put("iv", Base64.encodeToString(cipher.iv, Base64.NO_WRAP))
            put("ciphertext", Base64.encodeToString(cipher.doFinal(session.toString().toByteArray(Charsets.UTF_8)), Base64.NO_WRAP))
        }.toString()
        check(prefs.edit().clear().putString(ENVELOPE, envelope).commit())
        cachedEnvelope = envelope
        cachedSession = session
    }

    private fun decrypt(envelope: String): JSONObject {
        val encrypted = JSONObject(envelope)
        val iv = Base64.decode(encrypted.getString("iv"), Base64.NO_WRAP)
        require(iv.size == 12)
        val key = keyStore().getKey(KEY_ALIAS, null) as? SecretKey ?: error("Session key unavailable")
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
        cipher.updateAAD(AAD)
        val plaintext = cipher.doFinal(Base64.decode(encrypted.getString("ciphertext"), Base64.NO_WRAP))
        return JSONObject(String(plaintext, Charsets.UTF_8))
    }

    private fun keyStore(): KeyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun encryptionKey(): SecretKey {
        (keyStore().getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .setKeySize(256)
            .build())
        return generator.generateKey()
    }
}
