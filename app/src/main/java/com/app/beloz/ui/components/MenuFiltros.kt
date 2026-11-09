package com.app.beloz.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.viewModel.FilterViewModel
import com.google.gson.Gson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuFiltros(navController: NavController, filterViewModel: FilterViewModel = viewModel()) {
    var sheet by remember { mutableStateOf<String?>(null) }
    var food by remember { mutableStateOf(emptyList<String>()) }
    var price by remember { mutableStateOf<Map<String, Any>?>(null) }
    var sort by remember { mutableStateOf<Map<String, Any>?>(null) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Comida", "Precio", "Ordenar").forEach { label ->
            BotonFiltros(label = label, modifier = Modifier.weight(1f)) { sheet = label }
        }
    }
    if (sheet != null) {
        ModalBottomSheet(onDismissRequest = { sheet = null }, containerColor = BelozColors.MintSurface,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
            when (sheet) {
                "Comida" -> TipoComidaDesplegable(filterViewModel.foodTypeOptions, food, { food = it }) { selected ->
                    if (selected.isNotEmpty()) {
                        sheet = null
                        navController.navigate("tipo_comida_resultados/${Uri.encode(Gson().toJson(selected))}")
                    }
                }
                "Precio" -> OpcionesFiltro("Precio orientativo", filterViewModel.priceOptions, price, { price = it }) {
                    price?.let {
                        sheet = null
                        navController.navigate("precio_resultados/${Uri.encode(it["label"].toString())}")
                    }
                }
                else -> OpcionesFiltro("Ordenar restaurantes", filterViewModel.sortOptions, sort, { sort = it }) {
                    sort?.let {
                        sheet = null
                        val order = if (it["label"] == "Nombre A-Z") "nombre" else "espera"
                        navController.navigate("lista_restaurantes?sort=$order")
                    }
                }
            }
        }
    }
}

