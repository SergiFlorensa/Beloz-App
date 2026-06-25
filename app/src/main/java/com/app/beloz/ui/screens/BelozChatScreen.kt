package com.app.beloz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.beloz.R
import com.app.beloz.data.remote.ImageUrlResolver
import com.app.beloz.innovacion.chat.dominio.ChatMessage
import com.app.beloz.innovacion.chat.dominio.ChatRole
import com.app.beloz.innovacion.chat.dominio.ChatSuggestion
import com.app.beloz.innovacion.chat.presentacion.BelozChatViewModel
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BelozChatScreen(
    navController: NavController,
    viewModel: BelozChatViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(estado.messages.size, estado.loading) {
        if (estado.messages.isNotEmpty()) {
            listState.animateScrollToItem(estado.messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            BelozTopAppBar(
                title = "Beloz AI",
                subtitle = "Asistente de pedidos",
                navController = navController
            )
        },
        bottomBar = {
            ChatInputBar(
                value = input,
                loading = estado.loading,
                onValueChange = { input = it },
                onSend = {
                    viewModel.enviarMensaje(input)
                    input = ""
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
        ) {
            ChatHero()
            QuickPromptRow(
                enabled = !estado.loading,
                onPrompt = { prompt ->
                    viewModel.enviarMensaje(prompt)
                }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.messages) { message ->
                    ChatBubble(
                        message = message,
                        navController = navController
                    )
                }

                if (estado.loading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Surface(
                                color = Color.White,
                                shape = RoundedCornerShape(18.dp),
                                shadowElevation = 2.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp,
                                        color = BelozColors.Green
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text("Preparando ideas...", color = BelozColors.MutedText)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatHero() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        color = BelozColors.Ink,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(BelozColors.Ink, BelozColors.MutedGreen)
                    )
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = BelozColors.Green,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.chat_icon),
                        contentDescription = "Beloz AI",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Pide con contexto",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "Presupuesto, antojo, clima o prisa.",
                    color = Color(0xFFD9E8DE),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickPromptRow(
    enabled: Boolean,
    onPrompt: (String) -> Unit
) {
    val prompts = listOf(
        "Tengo 12 euros y quiero algo rapido",
        "Sorprendeme para cenar",
        "Quiero algo ligero",
        "Que pedirias si hace frio?"
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { prompt ->
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 2.dp,
                modifier = Modifier.clickable(enabled = enabled) { onPrompt(prompt) }
            ) {
                Text(
                    text = prompt,
                    color = BelozColors.Ink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    navController: NavController
) {
    val isUser = message.role == ChatRole.USER
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = if (isUser) BelozColors.Ink else Color.White,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                shadowElevation = if (isUser) 0.dp else 3.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else BelozColors.Ink,
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                    if (!message.provider.isNullOrBlank() && !isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (message.provider == "ollama") "IA local" else "Beloz motor local",
                            color = BelozColors.Green,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (message.suggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    message.suggestions.forEach { suggestion ->
                        SuggestionCard(
                            suggestion = suggestion,
                            onClick = {
                                navController.navigate("platos_restaurante/${suggestion.restauranteId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionCard(
    suggestion: ChatSuggestion,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = ImageUrlResolver.resolve(suggestion.imagePath)
            if (!imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = suggestion.restauranteNombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.size(10.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = suggestion.restauranteNombre,
                    color = BelozColors.Ink,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                suggestion.plato?.let {
                    Text(text = it, color = Color(0xFF45645B), fontSize = 12.sp)
                }
                Text(
                    text = suggestion.motivo ?: "Recomendado por Beloz AI",
                    color = BelozColors.MutedText,
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Text(
                text = ">",
                color = BelozColors.Green,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    loading: Boolean,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = BelozColors.MintSurface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Pregunta que pedir...") },
                shape = RoundedCornerShape(22.dp),
                maxLines = 3,
                enabled = !loading
            )
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = onSend,
                enabled = value.isNotBlank() && !loading,
                colors = ButtonDefaults.buttonColors(containerColor = BelozColors.Green),
                shape = CircleShape,
                modifier = Modifier.size(52.dp)
            ) {
                Text(">", color = BelozColors.Ink, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FloatingChatIcon(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        Surface(
            color = Color(0xFF10211C),
            shape = CircleShape,
            shadowElevation = 6.dp,
            modifier = Modifier
                .padding(16.dp)
                .size(58.dp)
                .clickable { navController.navigate("beloz_chat") }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.chat_icon),
                    contentDescription = "Beloz AI",
                    tint = Color(0xFF71CD9D),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
