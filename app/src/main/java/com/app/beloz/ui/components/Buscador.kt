package com.app.beloz.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.app.beloz.data.models.CatalogSearch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Buscador(
    navController: NavController,
    hintText: String = "Restaurantes, comidas...",
    onQueryChanged: (String) -> Unit,
    searchResults: List<String>,
    searchResultsIds: List<String>,
    searchResultKeywords: List<String> = emptyList(),
    onResultClick: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
    var showResults by remember { mutableStateOf(false) }
    var fieldWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val queryChanged by rememberUpdatedState(onQueryChanged)
    val currentQuery = text.trim()
    val rankedResults = remember(searchResults, searchResultsIds, searchResultKeywords, currentQuery) {
        rankSearchResults(searchResults, searchResultsIds, searchResultKeywords, currentQuery)
    }

    DisposableEffect(Unit) {
        onDispose { debounceJob?.cancel() }
    }

    fun submitSearch() {
        if (CatalogSearch.normalize(currentQuery).isEmpty()) return
        debounceJob?.cancel()
        showResults = false
        focusManager.clearFocus()
        navController.navigate("lista_restaurantes?query=${Uri.encode(currentQuery)}")
    }

    Column(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                debounceJob?.cancel()
                val query = newText.trim()
                showResults = query.isNotEmpty()
                if (query.isBlank()) {
                    queryChanged("")
                } else {
                    debounceJob = scope.launch {
                        delay(250)
                        queryChanged(query)
                    }
                }
            },
            placeholder = { Text(hintText, maxLines = 1, overflow = TextOverflow.Ellipsis) },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.SansSerif),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { submitSearch() }),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).onSizeChanged { fieldWidth = it.width },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BelozColors.Green,
                unfocusedBorderColor = BelozColors.MutedText.copy(alpha = 0.3f),
                focusedContainerColor = BelozColors.Card,
                unfocusedContainerColor = BelozColors.Card
            ),
            leadingIcon = {
                IconButton(onClick = { submitSearch() }) {
                    Icon(Icons.Default.Search, contentDescription = "Buscar restaurantes")
                }
            },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = {
                        debounceJob?.cancel()
                        text = ""
                        showResults = false
                        queryChanged("")
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Borrar busqueda")
                    }
                }
            }
        )

        if (showResults && rankedResults.isNotEmpty()) {
            Surface(color = BelozColors.Card, shape = RoundedCornerShape(8.dp)) {
            Column(Modifier.fillMaxWidth().heightIn(max = 240.dp).verticalScroll(rememberScrollState())) {
            rankedResults.forEach { result ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(result.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            if (result.keywords.isNotBlank()) {
                                Text(result.keywords, style = MaterialTheme.typography.bodySmall,
                                    color = BelozColors.MutedText, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    },
                    onClick = {
                        debounceJob?.cancel()
                        showResults = false
                        focusManager.clearFocus()
                        onResultClick(result.id)
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(text = { Text("Ver todos los resultados") }, onClick = { submitSearch() })
            }
            }
        }
    }
}

internal data class RankedSearchResult(val name: String, val id: String, val keywords: String)

internal fun rankSearchResults(
    names: List<String>, ids: List<String>, keywords: List<String>, query: String
): List<RankedSearchResult> = names.zip(ids).mapIndexedNotNull { index, (name, id) ->
    val item = RankedSearchResult(name, id, keywords.getOrNull(index).orEmpty())
    CatalogSearch.rank(item.name, item.keywords, query)?.let { item to it }
}.sortedWith(compareBy<Pair<RankedSearchResult, Int>> { it.second }
    .thenBy { CatalogSearch.normalize(it.first.name) })
    .map { it.first }.distinctBy { it.id }.take(8)
