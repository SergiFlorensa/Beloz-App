package com.app.beloz.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.data.models.Restaurante
import com.app.beloz.innovacion.decision.DecisionMode
import com.app.beloz.innovacion.decision.RestaurantDecision

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SugerenciasContextualesSection(navController: NavController, restaurants: List<Restaurante>,
    loading: Boolean, error: String?, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    var mode by remember { mutableStateOf(DecisionMode.DISCOVER) }
    var compare by remember { mutableStateOf(false) }
    val choices = remember(restaurants, mode) { RestaurantDecision.choose(restaurants, mode) }
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Tres opciones. Tú eliges.", style = MaterialTheme.typography.titleLarge, color = BelozColors.Ink)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DecisionMode.values().forEach { item ->
                FilterChip(selected = mode == item, onClick = { mode = item }, label = { Text(item.label) })
            }
        }
        when {
            loading -> Box(Modifier.fillMaxWidth().height(140.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> {
                Text(error, color = BelozColors.MutedText)
                OutlinedButton(onClick = onRetry) { Text("Reintentar") }
            }
            choices.isEmpty() -> Text("No hay opciones con estos datos.", color = BelozColors.MutedText)
            else -> {
                choices.forEachIndexed { index, choice ->
                    val restaurant = choice.restaurant
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable(role = Role.Button,
                            onClickLabel = "Ver carta de ${restaurant.name}") {
                            navController.navigate("platos_restaurante/${restaurant.restauranteId}")
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BelozColors.Outline),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (index == 0) 3.dp else 0.dp)
                    ) {
                        if (index == 0) CatalogImage(restaurant.imagePath, restaurant.name,
                            Modifier.fillMaxWidth().height(140.dp))
                        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            if (index != 0) CatalogImage(restaurant.imagePath, restaurant.name, Modifier.size(60.dp))
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(restaurant.name, style = MaterialTheme.typography.titleMedium, color = BelozColors.Ink)
                                Text(choice.reason, style = MaterialTheme.typography.bodySmall, color = BelozColors.MutedText)
                                Text("${RestaurantDecision.priceLabel(restaurant.priceLevel)} · ${if (restaurant.waitTime > 0) "~${restaurant.waitTime} min" else "Espera sin dato"}",
                                    style = MaterialTheme.typography.labelMedium, color = BelozColors.MutedGreen)
                            }
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = BelozColors.MutedGreen)
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Precio y espera orientativos", style = MaterialTheme.typography.labelSmall,
                        color = BelozColors.MutedText, modifier = Modifier.weight(1f))
                    TextButton(onClick = { compare = true }) { Text("Comparar") }
                }
            }
        }
        HorizontalDivider(color = BelozColors.Outline)
        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).clickable(role = Role.Button) {
            navController.navigate("beloz_chat?prompt=${Uri.encode("Tengo 12 euros. Que platos puedo pedir?")}")
        }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(Modifier.weight(1f)) {
                Text("¿Tienes un presupuesto?", style = MaterialTheme.typography.titleMedium, color = BelozColors.Ink)
                Text("Consulta platos con Beloz AI", style = MaterialTheme.typography.bodyMedium, color = BelozColors.MutedText)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = BelozColors.MutedGreen)
        }
    }
    if (compare) {
        ModalBottomSheet(onDismissRequest = { compare = false }, containerColor = BelozColors.MintSurface) {
            Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Ponlos en perspectiva", style = MaterialTheme.typography.titleLarge)
                choices.forEach { choice ->
                    val restaurant = choice.restaurant
                    Text(restaurant.name, style = MaterialTheme.typography.titleMedium)
                    Text("${restaurant.typeOfFood} · Precio: ${RestaurantDecision.priceLabel(restaurant.priceLevel)}",
                        style = MaterialTheme.typography.bodyMedium)
                    Text(if (restaurant.waitTime > 0) "Espera registrada: ${restaurant.waitTime} min aprox." else "Espera no disponible",
                        style = MaterialTheme.typography.bodySmall, color = BelozColors.MutedText)
                    TextButton(onClick = {
                        compare = false
                        navController.navigate("platos_restaurante/${restaurant.restauranteId}")
                    }) { Text("Ver carta") }
                    HorizontalDivider(color = BelozColors.Outline)
                }
                Text("Estimaciones del catálogo; confirma condiciones con el restaurante.",
                    style = MaterialTheme.typography.bodySmall, color = BelozColors.MutedText)
            }
        }
    }
}
