package com.app.beloz.data.remote

import com.app.beloz.BuildConfig

object SupabaseConfig {
    private const val DEFAULT_SUPABASE_URL = "https://vcsbipotujymyxzbsejq.supabase.co"
    private const val DEFAULT_SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZjc2JpcG90dWp5bXl4emJzZWpxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjcxOTIyNDgsImV4cCI6MjA4Mjc2ODI0OH0.6SjkK0Pja7AmtKTpgHWlECbmri9_uK8Pidhf2W4eX7k"
    private const val DEFAULT_STORAGE_BUCKET = "images"

    val SUPABASE_URL: String =
        if (BuildConfig.SUPABASE_URL.isNotBlank()) BuildConfig.SUPABASE_URL else DEFAULT_SUPABASE_URL
    val SUPABASE_ANON_KEY: String =
        if (BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) BuildConfig.SUPABASE_ANON_KEY else DEFAULT_SUPABASE_ANON_KEY
    val STORAGE_BUCKET: String =
        if (BuildConfig.SUPABASE_STORAGE_BUCKET.isNotBlank()) BuildConfig.SUPABASE_STORAGE_BUCKET else DEFAULT_STORAGE_BUCKET
    val REST_BASE_URL: String = "$SUPABASE_URL/rest/v1/"
    val IMAGES_BASE_URL: String = "$SUPABASE_URL/storage/v1/object/public/$STORAGE_BUCKET/"
}
