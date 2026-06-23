package com.app.beloz.data.remote

import com.app.beloz.BuildConfig

object ImageUrlResolver {
    fun resolve(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) {
            path
        } else {
            "${backendBaseUrl()}images/${path.trimStart('/')}"
        }
    }

    private fun backendBaseUrl(): String {
        val baseUrl = BuildConfig.BELOZ_API_BASE_URL.trim()
        return if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    }
}
