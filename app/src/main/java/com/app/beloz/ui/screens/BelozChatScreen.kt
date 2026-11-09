package com.app.beloz.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.beloz.R
import com.app.beloz.data.remote.ImageUrlResolver
import com.app.beloz.innovacion.chat.dominio.ChatMessage
import com.app.beloz.innovacion.chat.dominio.ChatRole
import com.app.beloz.innovacion.chat.dominio.ChatSource
import com.app.beloz.innovacion.chat.dominio.ChatSuggestion
import com.app.beloz.innovacion.chat.presentacion.BelozChatViewModel
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Locale

private val ChatBody = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
    color = BelozColors.Ink
)
private val ChatCaption = ChatBody.copy(fontSize = 12.sp, lineHeight = 18.sp, color = BelozColors.MutedText)

@Composable
fun BelozChatScreen(
    navController: NavController,
    initialPrompt: String = "",
    viewModel: BelozChatViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    val listState = rememberLazyListState()
    var initialPromptConsumed by rememberSaveable(initialPrompt) { mutableStateOf(false) }
    val isStart = estado.messages.none { it.role == ChatRole.USER }

    LaunchedEffect(initialPrompt) {
        if (!initialPromptConsumed && initialPrompt.isNotBlank()) {
            initialPromptConsumed = true
            viewModel.enviarMensaje(initialPrompt)
        }
    }
    LaunchedEffect(estado.messages.size, estado.loading, estado.error) {
        val lastIndex = estado.messages.size - 1 + if (estado.loading || estado.error != null) 1 else 0
        if (lastIndex >= 0) listState.animateScrollToItem(lastIndex)
    }

    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = { BelozTopAppBar(title = "Beloz", navController = navController) },
        bottomBar = {
            ChatInputBar(
                value = estado.draft,
                loading = estado.loading,
                onValueChange = viewModel::actualizarBorrador,
                onSend = { viewModel.enviarMensaje(estado.draft) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues).consumeWindowInsets(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.widthIn(max = 720.dp).fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(estado.messages, key = { it.id }) { message ->
                    ChatMessageBlock(message, navController)
                }
                if (estado.loading) item(key = "loading") { WaitingNote() }
                estado.error?.let { error ->
                    item(key = "error") {
                        Column(modifier = Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite }) {
                            Text(error, style = ChatBody, color = Color(0xFF9A342F))
                            TextButton(onClick = viewModel::reintentar, modifier = Modifier.heightIn(min = 48.dp)) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = BelozColors.MutedGreen)
                                Spacer(Modifier.width(8.dp))
                                Text("Reintentar", style = ChatBody, color = BelozColors.MutedGreen)
                            }
                        }
                    }
                }
                if (isStart) {
                    item(key = "prompts") {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("Platos hasta 12 EUR", "Quiero cenar rápido", "Ver una carta").forEach { prompt ->
                                TextButton(
                                    onClick = {
                                        val query = when (prompt) {
                                            "Platos hasta 12 EUR" -> "Recomiéndame platos de hasta 12 EUR"
                                            "Ver una carta" -> "Quiero ver una carta"
                                            else -> prompt
                                        }
                                        viewModel.enviarMensaje(query)
                                    },
                                    enabled = !estado.loading,
                                    modifier = Modifier.heightIn(min = 48.dp)
                                ) {
                                    Text(prompt, style = ChatBody, color = BelozColors.MutedGreen, modifier = Modifier.weight(1f, fill = false))
                                    Spacer(Modifier.width(12.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BelozColors.MutedGreen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageBlock(message: ChatMessage, navController: NavController) {
    val user = message.role == ChatRole.USER
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (user) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(if (user) "Tú" else "Beloz", style = ChatCaption, fontWeight = FontWeight.SemiBold)
        if (user) {
            Surface(color = Color(0xFFBFEFD9), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth(0.9f)) {
                SelectionContainer { Text(message.text, style = ChatBody, modifier = Modifier.padding(12.dp)) }
            }
        } else {
            SelectionContainer { Text(message.text, style = ChatBody) }
        }
        message.suggestions.take(3).forEach { suggestion ->
            MenuSuggestionCard(suggestion) {
                navController.navigate("platos_restaurante/${suggestion.restauranteId}")
            }
        }
        if (!user && message.suggestions.isEmpty()) message.sources.distinct().forEach { SourceNote(it) }
    }
}

@Composable
private fun MenuSuggestionCard(suggestion: ChatSuggestion, onOpen: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BelozColors.Outline)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                val imageUrl = ImageUrlResolver.resolve(suggestion.imagePath)
                if (!imageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(suggestion.plato ?: suggestion.restauranteNombre, style = ChatBody, fontWeight = FontWeight.SemiBold)
                    if (!suggestion.plato.isNullOrBlank()) Text(suggestion.restauranteNombre, style = ChatCaption)
                    Text(
                        suggestion.price?.takeIf { it.isFinite() && it >= 0 }?.let { String.format(Locale.forLanguageTag("es-ES"), "%.2f EUR", it) } ?: "Precio no disponible",
                        style = ChatBody, color = BelozColors.MutedGreen, fontWeight = FontWeight.SemiBold
                    )
                    suggestion.waitTime?.takeIf { it >= 0 }?.let { Text("Espera registrada: $it min", style = ChatCaption) }
                }
            }
            SourceNote(suggestion.source)
            TextButton(onClick = onOpen, contentPadding = PaddingValues(horizontal = 0.dp), modifier = Modifier.heightIn(min = 48.dp)) {
                Text("Ver carta", style = ChatBody, color = BelozColors.MutedGreen)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = BelozColors.MutedGreen)
            }
        }
    }
}

@Composable
private fun SourceNote(source: ChatSource?) {
    val uriHandler = LocalUriHandler.current
    val safeUrl = source?.url?.takeIf { value ->
        runCatching { URI(value).let { it.scheme in listOf("https", "http") && it.host != null && it.userInfo == null } }.getOrDefault(false)
    }
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(source?.label ?: "Catálogo Beloz", style = ChatCaption)
        Text(
            source?.updatedAt?.let(::formatSourceDate)?.let { "Registro actualizado: $it" } ?: "Fecha de actualización no disponible",
            style = ChatCaption
        )
        if (safeUrl != null) TextButton(onClick = { runCatching { uriHandler.openUri(safeUrl) } }, modifier = Modifier.heightIn(min = 48.dp)) {
            Text("Abrir fuente", style = ChatCaption, color = BelozColors.MutedGreen)
        }
    }
}

private fun formatSourceDate(value: String): String? = runCatching {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }.parse(value.take(10))
    date?.let { SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-ES")).format(it) }
}.getOrNull()

@Composable
private fun WaitingNote() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }
    ) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = BelozColors.MutedGreen)
        Spacer(Modifier.width(12.dp))
        Text("Esperando respuesta…", style = ChatBody, color = BelozColors.MutedText)
    }
}

@Composable
private fun ChatInputBar(value: String, loading: Boolean, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(color = Color.White) {
        Box(Modifier.fillMaxWidth().navigationBarsPadding().imePadding(), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier.widthIn(max = 720.dp).fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Mensaje", style = ChatCaption) },
                    placeholder = { Text("¿Qué te apetece?", style = ChatBody, color = BelozColors.MutedText) },
                    textStyle = ChatBody,
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { if (!loading && value.isNotBlank()) onSend() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BelozColors.MutedGreen,
                        unfocusedBorderColor = BelozColors.Outline,
                        cursorColor = BelozColors.MutedGreen
                    )
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank() && !loading,
                    modifier = Modifier.padding(bottom = 4.dp).size(48.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = BelozColors.MutedGreen, contentColor = Color.White)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar mensaje", modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
fun FloatingChatIcon(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart) {
        FilledIconButton(
            onClick = { navController.navigate("beloz_chat") },
            modifier = Modifier.padding(16.dp).size(56.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = BelozColors.MutedGreen)
        ) {
            Image(painter = painterResource(R.drawable.chat_icon), contentDescription = "Abrir chat Beloz", modifier = Modifier.size(28.dp))
        }
    }
}
