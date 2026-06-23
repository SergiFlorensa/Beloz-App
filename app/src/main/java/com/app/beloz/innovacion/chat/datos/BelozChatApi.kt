package com.app.beloz.innovacion.chat.datos

import com.app.beloz.innovacion.chat.dominio.ChatRequest
import com.app.beloz.innovacion.chat.dominio.ChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BelozChatApi {
    @POST("api/chat")
    suspend fun enviarMensaje(@Body request: ChatRequest): ChatResponse
}
