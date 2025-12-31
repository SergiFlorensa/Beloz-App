package com.app.beloz.data.remote

object SupabaseConfig {
    const val SUPABASE_URL = "https://vcsbipotujymyxzbsejq.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_secret_5Bhrxulag2_yoKEJVJO9Qg_gE-tlUwo"
    const val REST_BASE_URL = "$SUPABASE_URL/rest/v1/"
    const val STORAGE_BUCKET = "images"
    const val IMAGES_BASE_URL = "$SUPABASE_URL/storage/v1/object/public/$STORAGE_BUCKET/"
}
