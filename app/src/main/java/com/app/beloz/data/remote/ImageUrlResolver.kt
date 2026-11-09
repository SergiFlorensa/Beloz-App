package com.app.beloz.data.remote

import com.app.beloz.BuildConfig
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

object ImageUrlResolver {
    fun resolve(path: String?): String? {
        if (path.isNullOrBlank()) return null
        val value = path.trim()
        val absolute = value.toHttpUrlOrNull()
        if (absolute != null) {
            if (absolute.username.isNotEmpty() || absolute.password.isNotEmpty()) return null
            if (!BuildConfig.DEBUG && !absolute.isHttps) return null
            return absolute.toString()
        }
        val relative = value.trimStart('/').removePrefix("images/")
        if (':' in relative || '\\' in relative || relative.split('/').any { it == ".." }) return null
        // Existing catalog references omit the final extension of these two bundled assets.
        val asset = when (relative) {
            "poke_vegano.jpg", "poke_camaron.jpg" -> "$relative.png"
            else -> relative
        }
        return "${backendBaseUrl()}images/$asset".toHttpUrlOrNull()?.toString()
    }

    private fun backendBaseUrl(): String {
        val baseUrl = BuildConfig.BELOZ_API_BASE_URL.trim()
        return if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    }
}
