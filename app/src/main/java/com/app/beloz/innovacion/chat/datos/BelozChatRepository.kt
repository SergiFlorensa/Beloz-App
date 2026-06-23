package com.app.beloz.innovacion.chat.datos

import com.app.beloz.data.remote.BelozApiClient
import com.app.beloz.innovacion.chat.dominio.ChatRequest
import com.app.beloz.innovacion.chat.dominio.ChatResponse

class BelozChatRepository {
    private val api: BelozChatApi by lazy {
        BelozApiClient.retrofit.create(BelozChatApi::class.java)
    }

    suspend fun enviarMensaje(request: ChatRequest): ChatResponse {
        return api.enviarMensaje(request)
    }
}
