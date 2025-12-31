package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.theme.DanfordFontFamily
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun Buscador(
    navController: NavController,
    hintText: String = "Restaurantes, comidas...",
    onQueryChanged: (String) -> Unit,
    searchResults: List<String>,
    searchResultsIds: List<String>,
    onResultClick: (String) -> Unit
) {
    var textState by remember { mutableStateOf(TextFieldValue()) }
    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    var showResults by remember { mutableStateOf(false) }

    val textFieldSize = remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    val textFieldOffset = remember { mutableStateOf(Offset.Zero) }

    val textFieldModifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .onGloballyPositioned { coordinates ->
            textFieldSize.value = coordinates.size.toSize()
            textFieldOffset.value = coordinates.localToWindow(Offset.Zero)
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column {
            TextField(
                value = textState,
                onValueChange = { newValue ->
                    textState = newValue
                    debounceJob?.cancel()
                    debounceJob = coroutineScope.launch {
                        delay(300)
                        val query = newValue.text.trim()
                        onQueryChanged(query)
                        showResults = query.isNotEmpty() && searchResults.isNotEmpty()
                    }
                },
                placeholder = {
                    Text(
                        text = hintText,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontFamily = DanfordFontFamily,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth(),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = DanfordFontFamily
                ),
                modifier = textFieldModifier,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black,
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.lupa),
                        contentDescription = "Buscar",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                val query = textState.text.trim()
                                if (query.isNotEmpty()) {
                                    navController.navigate("lista_restaurantes?query=${URLEncoder.encode(query, "UTF-8")}")
                                }
                            }
                    )
                },
                trailingIcon = {
                    if (textState.text.isNotEmpty()) {
                        IconButton(onClick = {
                            textState = TextFieldValue("")
                            onQueryChanged("")
                            showResults = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.eliminar),
                                contentDescription = "Limpiar texto",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            if (showResults) {
                Popup(
                    alignment = Alignment.TopStart,
                    offset = IntOffset(
                        x = textFieldOffset.value.x.toInt(),
                        y = (textFieldOffset.value.y + textFieldSize.value.height).toInt()
                    ),
                    properties = PopupProperties(focusable = false),
                    onDismissRequest = { showResults = false }
                ) {
                    Card(
                        modifier = Modifier
                            .width(textFieldSize.value.width.dp)
                            .padding(horizontal = 16.dp)
                            .shadow(8.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .heightIn(max = 300.dp)
                        ) {
                            itemsIndexed(searchResults) { index, result ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onResultClick(searchResultsIds[index])
                                            showResults = false
                                        }
                                        .padding(horizontal = 16.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = result,
                                        fontSize = 12.sp,
                                        fontFamily = DanfordFontFamily,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (index < searchResults.size - 1) {
                                        Divider(
                                            color = Color(0xFF71CD9D),
                                            thickness = 1.dp,
                                            modifier = Modifier.padding(top = 12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
