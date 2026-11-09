package com.app.beloz.innovacion.chat.presentacion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class BelozChatViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    private val repository = BelozChatRepository()
    private val perfilRepo = PerfilSaborRepository(application)
    private var perfilActual = PerfilSabor()

    private val _estado = MutableStateFlow(BelozChatUiState(draft = savedStateHandle["chat_draft"] ?: ""))
    val estado: StateFlow<BelozChatUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            perfilRepo.perfilFlow().collect { perfil ->
                perfilActual = perfil
            }
        }
    }

    fun actualizarBorrador(text: String) {
        val draft = text.take(2000)
        savedStateHandle["chat_draft"] = draft
        _estado.value = _estado.value.copy(draft = draft)
    }

    fun reintentar() {
        _estado.value.failedMessage?.let(::enviarMensaje)
    }

    fun enviarMensaje(text: String) {
        val message = text.trim()
        if (message.isBlank() || message.length > 2000 || _estado.value.loading) return
        if (_estado.value.draft.isBlank()) actualizarBorrador(text)
        val retry = _estado.value.failedMessage == message &&
            _estado.value.messages.lastOrNull()?.let { it.role == ChatRole.USER && it.text == message } == true
        val currentMessages = if (retry) _estado.value.messages else
            _estado.value.messages + ChatMessage(ChatRole.USER, message)
        // Set this synchronously so repeated taps cannot start parallel requests.
        _estado.value = _estado.value.copy(messages = currentMessages, loading = true, error = null, failedMessage = null)

        viewModelScope.launch {
            try {
                val response = withTimeout(25_000) {
                    repository.enviarMensaje(
                        ChatRequest(message = message, perfilSabor = perfilActual.toContextual(),
                            history = currentMessages.dropLast(1).filter { it.role == ChatRole.USER }.takeLast(4).map { it.text })
                    )
                }
                check(!response.respuesta.isNullOrBlank())
                if (_estado.value.draft.trim() == message) actualizarBorrador("")

                _estado.value = _estado.value.copy(
                    messages = currentMessages + ChatMessage(
                        role = ChatRole.ASSISTANT,
                        text = response.respuesta,
                        provider = response.provider,
                        suggestions = response.sugerencias.orEmpty().filter { it.restauranteId > 0 },
                        sources = response.sources.orEmpty()
                    ),
                    loading = false
                )
            } catch (e: Exception) {
                if (e is CancellationException && e !is TimeoutCancellationException) throw e
                val error = when (e) {
                    is UnknownHostException -> "Sin conexión con Beloz. Revisa tu conexión a Internet y vuelve a intentarlo."
                    is SocketTimeoutException, is TimeoutCancellationException -> "Beloz ha tardado demasiado en responder. Puedes reintentar."
                    is HttpException -> if (e.code() == 503) "El catálogo no está disponible ahora. Puedes reintentar en unos instantes." else "No se ha podido completar la consulta. Puedes reintentar."
                    is IOException -> "No se ha podido conectar con Beloz. Revisa tu conexión y vuelve a intentarlo."
                    else -> "No se ha recibido una respuesta válida. Puedes reintentar."
                }
                _estado.value = _estado.value.copy(
                    loading = false,
                    error = error,
                    failedMessage = message
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
            text = "¿Qué te apetece comer?",
            provider = "beloz"
        )
    ),
    val loading: Boolean = false,
    val error: String? = null,
    val draft: String = "",
    val failedMessage: String? = null
)
