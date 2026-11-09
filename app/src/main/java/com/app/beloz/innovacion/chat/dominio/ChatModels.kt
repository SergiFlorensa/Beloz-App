package com.app.beloz.innovacion.chat.dominio

import com.app.beloz.innovacion.contexto.dominio.PerfilSaborContextual
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class ChatRequest(
    val message: String,
    @SerializedName("perfil_sabor")
    val perfilSabor: PerfilSaborContextual? = null,
    val history: List<String> = emptyList()
)

data class ChatResponse(
    val provider: String,
    val respuesta: String,
    val accion: String,
    val sugerencias: List<ChatSuggestion> = emptyList(),
    val sources: List<ChatSource>? = null,
    @SerializedName("retrieved_at")
    val retrievedAt: String? = null
)

data class ChatSource(
    val label: String? = null,
    val url: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("retrieved_at")
    val retrievedAt: String? = null
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
    val motivo: String?,
    @SerializedName("plato_id")
    val platoId: Int? = null,
    val source: ChatSource? = null
)

enum class ChatRole {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val role: ChatRole,
    val text: String,
    val provider: String? = null,
    val suggestions: List<ChatSuggestion> = emptyList(),
    val sources: List<ChatSource> = emptyList(),
    val id: String = UUID.randomUUID().toString()
)
