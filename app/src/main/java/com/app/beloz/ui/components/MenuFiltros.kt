package com.app.beloz.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.ui.viewModel.FilterViewModel
import com.google.gson.Gson
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun MenuFiltros(navController: NavController, filterViewModel: FilterViewModel = viewModel()) {
    val options = filterViewModel.foodTypeOptions
    val priceOptions = filterViewModel.priceOptions
    val sortOptions = filterViewModel.sortOptions
    val context = LocalContext.current

    var selectedFoodTypes by remember { mutableStateOf(listOf<String>()) }
    var selectedPriceOption by remember { mutableStateOf<Map<String, Any>?>(null) }
    var selectedSortOption by remember { mutableStateOf<Map<String, Any>?>(null) }

    var showFoodTypeOptions by remember { mutableStateOf(false) }
    var showPriceOptions by remember { mutableStateOf(false) }
    var showSortOptions by remember { mutableStateOf(false) }

    fun toggleDropdown(dropdownType: String) {
        when (dropdownType) {
            "Tipo de comida" -> {
                showFoodTypeOptions = !showFoodTypeOptions
                if (showFoodTypeOptions) {
                    showPriceOptions = false
                    showSortOptions = false
                }
            }
            "Precio" -> {
                showPriceOptions = !showPriceOptions
                if (showPriceOptions) {
                    showFoodTypeOptions = false
                    showSortOptions = false
                }
            }
            "Ordenar por" -> {
                showSortOptions = !showSortOptions
                if (showSortOptions) {
                    showFoodTypeOptions = false
                    showPriceOptions = false
                }
            }
        }
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BotonFiltros(label = "Tipo de comida") {
                toggleDropdown("Tipo de comida")
            }
            BotonFiltros(label = "Precio") {
                toggleDropdown("Precio")
            }
            BotonFiltros(label = "Ordenar por") {
                toggleDropdown("Ordenar por")
            }
        }

        if (showFoodTypeOptions) {

        }
        if (showFoodTypeOptions) {
            TipoComidaDesplegable(
                options = options,
                selectedOptions = selectedFoodTypes,
                onOptionSelected = { selected ->
                    selectedFoodTypes = selected
                },
                onShowResults = { selectedTypes ->
                    if (selectedTypes.isNotEmpty()) {
                        val selectedTypesJson = Uri.encode(Gson().toJson(selectedTypes))
                        navController.navigate("tipo_comida_resultados/$selectedTypesJson")
                        showFoodTypeOptions = false
                    } else {
                        Toast.makeText(
                            context,
                            "Selecciona al menos un tipo de comida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
        if (showPriceOptions) {
            OpcionesFiltro(
                title = "Precio",
                options = priceOptions,
                selectedOption = selectedPriceOption,
                onOptionSelected = { selected ->
                    selectedPriceOption = selected
                },
                onShowResults = {
                    if (selectedPriceOption != null) {
                        val label = selectedPriceOption!!["label"] as String
                        val selectedPriceEncoded = Uri.encode(label)
                        navController.navigate("precio_resultados/$selectedPriceEncoded")
                        showPriceOptions = false
                    } else {
                        Toast.makeText(
                            context,
                            "Selecciona un nivel de precio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        if (showSortOptions) {
            OpcionesFiltro(
                title = "Ordenar por",
                options = sortOptions,
                selectedOption = selectedSortOption,
                onOptionSelected = { selected ->
                    selectedSortOption = selected
                },
                onShowResults = {
                    if (selectedSortOption != null) {
                        val label = selectedSortOption!!["label"] as String
                        when (label) {
                            "Relevancia" -> {
                                navController.navigate("relevancia_restaurantes")
                            }
                            "Valoración" -> {
                                navController.navigate("valoracion_restaurantes")
                            }
                            else -> {
                                Toast.makeText(
                                    context,
                                    "Opción no reconocida",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showSortOptions = false
                    } else {
                        Toast.makeText(
                            context,
                            "Selecciona una opción de orden",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }
    }
}

