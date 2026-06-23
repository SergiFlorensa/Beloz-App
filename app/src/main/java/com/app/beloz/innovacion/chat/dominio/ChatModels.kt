package com.app.beloz.innovacion.chat.dominio

import com.app.beloz.innovacion.contexto.dominio.PerfilSaborContextual
import com.google.gson.annotations.SerializedName

data class ChatRequest(
    val message: String,
    @SerializedName("perfil_sabor")
    val perfilSabor: PerfilSaborContextual? = null
)

data class ChatResponse(
    val provider: String,
    val respuesta: String,
    val accion: String,
    val sugerencias: List<ChatSuggestion> = emptyList()
)

data class ChatSuggestion(
    @SerializedName("restaurante_id")
    val restauranteId: Int,
    @SerializedName("restaurante_nombre")
    val restauranteNombre: String,
    @SerializedName("image_path")
    val imagePath: String?,
    val plato: String?,
    val price: Double?,
    @SerializedName("wait_time")
    val waitTime: Int?,
    @SerializedName("type_of_food")
    val typeOfFood: String?,
    val motivo: String?
)

enum class ChatRole {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val role: ChatRole,
    val text: String,
    val provider: String? = null,
    val suggestions: List<ChatSuggestion> = emptyList()
)
