package com.app.beloz.data.remote

object ImageUrlResolver {
    fun resolve(path: String?): String? {
        if (path.isNullOrBlank()) return null
        return if (path.startsWith("http")) {
            path
        } else {
            SupabaseConfig.IMAGES_BASE_URL + path.trimStart('/')
        }
    }
}
