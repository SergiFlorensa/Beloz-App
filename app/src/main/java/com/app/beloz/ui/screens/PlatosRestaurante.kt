package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.innovacion.perfil.EventoUso
import com.app.beloz.innovacion.perfil.PerfilSaborRepository
import com.app.beloz.innovacion.perfil.TipoEvento
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.MainScaffold
import com.app.beloz.ui.components.PlatoCard
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.PlatosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun PlatosRestaurante(
    restauranteId: Int,
    navController: NavController,
    viewModel: PlatosViewModel = remember { PlatosViewModel() },
    cartViewModel: CartViewModel
) {
    val platos by viewModel.platos.collectAsState()
    val context = LocalContext.current
    val perfilRepo = remember { PerfilSaborRepository(context) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(restauranteId) {
        viewModel.cargarPlatosPorRestaurante(restauranteId)
    }

    var showDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    MainScaffold(
        navController = navController,
        cartViewModel = cartViewModel,
        topBar = {
            BelozTopAppBar(
                title = "Platos",
                subtitle = "Elige y anade al carrito",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(16.dp)
        ) {
            if (platos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay platos disponibles para este restaurante",
                        modifier = Modifier.padding(16.dp),
                        color = BelozColors.MutedText
                    )
                }
            } else {
                val groupedPlatos = remember(platos) {
                    platos.groupBy { plato ->
                        plato.category?.takeIf { it.isNotBlank() } ?: "Carta"
                    }
                }
                val categories = groupedPlatos.keys.toList()
                var selectedCategory by remember(restauranteId) { mutableStateOf<String?>(null) }

                LaunchedEffect(categories) {
                    if (selectedCategory !in categories) {
                        selectedCategory = categories.firstOrNull()
                    }
                }

                val activeCategory = selectedCategory ?: categories.firstOrNull()
                val visiblePlatos = groupedPlatos[activeCategory].orEmpty()

                MenuCategorySelector(
                    categories = categories,
                    counts = groupedPlatos.mapValues { it.value.size },
                    selectedCategory = activeCategory,
                    onCategorySelected = { selectedCategory = it }
                )

                if (!activeCategory.isNullOrBlank()) {
                    PlatoSectionHeader(
                        title = activeCategory,
                        count = visiblePlatos.size
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(visiblePlatos, key = { plato -> plato.id }) { plato ->
                        PlatoCard(
                            imagePath = plato.imagePath,
                            name = plato.name,
                            description = plato.description,
                            price = plato.price,
                            onAddToCart = { quantity ->
                                val added = cartViewModel.addToCart(plato, quantity, restauranteId)
                                if (!added) {
                                    dialogMessage = "Solo puedes anadir platos de un mismo restaurante al carrito."
                                    showDialog = true
                                } else {
                                    scope.launch {
                                        perfilRepo.registrarEvento(
                                            EventoUso(
                                                tipo = TipoEvento.ADD_TO_CART,
                                                metadata = mapOf(
                                                    "restaurant_id" to restauranteId.toString(),
                                                    "plato_id" to plato.id.toString(),
                                                    "price" to plato.price.toString(),
                                                    "quantity" to quantity.toString(),
                                                    "category" to (plato.category ?: "Carta")
                                                )
                                            )
                                        )
                                    }
                                }
                            },
                            onClick = {}
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Error") },
                text = { Text(dialogMessage) }
            )
        }
    }
}

@Composable
private fun MenuCategorySelector(
    categories: List<String>,
    counts: Map<String, Int>,
    selectedCategory: String?,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories, key = { it }) { category ->
            val selected = category == selectedCategory
            Surface(
                modifier = Modifier
                    .heightIn(min = 44.dp)
                    .widthIn(min = 92.dp)
                    .clickable { onCategorySelected(category) },
                shape = RoundedCornerShape(16.dp),
                color = if (selected) BelozColors.Ink else BelozColors.Card,
                shadowElevation = if (selected) 4.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (selected) BelozColors.Green else BelozColors.Orange,
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    Column {
                        Text(
                            text = category,
                            color = if (selected) Color.White else BelozColors.Ink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${counts[category] ?: 0} platos",
                            color = if (selected) BelozColors.Green else BelozColors.MutedText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlatoSectionHeader(
    title: String,
    count: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 2.dp),
        shape = RoundedCornerShape(18.dp),
        color = BelozColors.Ink
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = count.toString(),
                color = BelozColors.Green,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
