package com.app.beloz.innovacion.chat.presentacion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.innovacion.chat.datos.BelozChatRepository
import com.app.beloz.innovacion.chat.dominio.ChatMessage
import com.app.beloz.innovacion.chat.dominio.ChatRequest
import com.app.beloz.innovacion.chat.dominio.ChatRole
import com.app.beloz.innovacion.contexto.dominio.ConteoContextual
import com.app.beloz.innovacion.contexto.dominio.PerfilSaborContextual
import com.app.beloz.innovacion.perfil.PerfilSabor
import com.app.beloz.innovacion.perfil.PerfilSaborRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BelozChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BelozChatRepository()
    private val perfilRepo = PerfilSaborRepository(application)
    private var perfilActual = PerfilSabor()

    private val _estado = MutableStateFlow(BelozChatUiState())
    val estado: StateFlow<BelozChatUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            perfilRepo.perfilFlow().collect { perfil ->
                perfilActual = perfil
            }
        }
    }

    fun enviarMensaje(text: String) {
        val message = text.trim()
        if (message.isBlank() || _estado.value.loading) return

        viewModelScope.launch {
            val currentMessages = _estado.value.messages + ChatMessage(ChatRole.USER, message)
            _estado.value = _estado.value.copy(
                messages = currentMessages,
                loading = true,
                error = null
            )

            try {
                val response = repository.enviarMensaje(
                    ChatRequest(
                        message = message,
                        perfilSabor = perfilActual.toContextual()
                    )
                )

                _estado.value = _estado.value.copy(
                    messages = currentMessages + ChatMessage(
                        role = ChatRole.ASSISTANT,
                        text = response.respuesta,
                        provider = response.provider,
                        suggestions = response.sugerencias
                    ),
                    loading = false
                )
            } catch (e: Exception) {
                _estado.value = _estado.value.copy(
                    messages = currentMessages + ChatMessage(
                        role = ChatRole.ASSISTANT,
                        text = "No he podido conectar con Beloz AI ahora mismo. Prueba otra vez en unos segundos.",
                        provider = "offline"
                    ),
                    loading = false,
                    error = e.message
                )
            }
        }
    }

    private fun PerfilSabor.toContextual(): PerfilSaborContextual {
        return PerfilSaborContextual(
            totalEventos = totalEventos,
            topTiposComida = topTiposComida.map { ConteoContextual(it.clave, it.conteo) },
            topRangosPrecio = topRangosPrecio.map { ConteoContextual(it.clave, it.conteo) },
            topRestaurantes = topRestaurantes.map { ConteoContextual(it.clave, it.conteo) }
        )
    }
}

data class BelozChatUiState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            role = ChatRole.ASSISTANT,
            text = "Dime que te apetece, tu presupuesto o si tienes prisa. Te recomiendo usando restaurantes reales de Beloz.",
            provider = "beloz"
        )
    ),
    val loading: Boolean = false,
    val error: String? = null
)
