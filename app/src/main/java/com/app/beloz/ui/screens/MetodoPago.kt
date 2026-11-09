package com.app.beloz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.viewModel.PaymentViewModel

@Composable
fun MetodoPago(navController: NavController, paymentViewModel: PaymentViewModel) {
    Scaffold(containerColor = BelozColors.MintSurface,
        topBar = { BelozTopAppBar("Pagos", navController) }) { padding ->
        Column(Modifier.padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Pagos no disponibles", style = MaterialTheme.typography.headlineSmall)
            Text("Beloz todavía no procesa cobros. No te pediremos datos de tarjeta.",
                style = MaterialTheme.typography.bodyLarge)
            Text("Puedes consultar las cartas y preparar tu selección en el carrito.",
                style = MaterialTheme.typography.bodyMedium, color = BelozColors.MutedText)
            TextButton(onClick = { navController.navigate("carrito") { launchSingleTop = true } }) {
                Text("Ir al carrito")
            }
        }
    }
}
