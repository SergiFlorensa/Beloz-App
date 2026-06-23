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
            TopAppBar(
                title = {
                    Column {
                        Text("Beloz AI", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Asistente de pedidos",
                            color = Color(0xFF45645B),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text(text = "<", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FBF7),
                    titleContentColor = Color(0xFF10211C)
                )
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
                .background(Color(0xFFF8FBF7))
                .padding(paddingValues)
        ) {
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
                                        color = Color(0xFF71CD9D)
                                    )
                                    Spacer(modifier = Modifier.size(10.dp))
                                    Text("Pensando...", color = Color(0xFF45645B))
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
                shadowElevation = 1.dp,
                modifier = Modifier.clickable(enabled = enabled) { onPrompt(prompt) }
            ) {
                Text(
                    text = prompt,
                    color = Color(0xFF244A3D),
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
                color = if (isUser) Color(0xFF10211C) else Color.White,
                shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                shadowElevation = if (isUser) 0.dp else 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        color = if (isUser) Color.White else Color(0xFF10211C),
                        fontSize = 14.sp,
                        lineHeight = 19.sp
                    )
                    if (!message.provider.isNullOrBlank() && !isUser) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (message.provider == "ollama") "Gemma 12B local" else "Beloz motor local",
                            color = Color(0xFF71CD9D),
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
                    color = Color(0xFF10211C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                suggestion.plato?.let {
                    Text(text = it, color = Color(0xFF45645B), fontSize = 12.sp)
                }
                Text(
                    text = suggestion.motivo ?: "Recomendado por Beloz AI",
                    color = Color(0xFF6B7D76),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }

            Text(
                text = ">",
                color = Color(0xFF71CD9D),
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
        color = Color(0xFFF8FBF7),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF71CD9D)),
                shape = CircleShape,
                modifier = Modifier.size(52.dp)
            ) {
                Text(">", color = Color.Black, fontWeight = FontWeight.Bold)
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
