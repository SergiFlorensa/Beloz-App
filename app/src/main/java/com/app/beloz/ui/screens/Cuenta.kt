package com.app.beloz.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.innovacion.perfil.PerfilSaborRepository
import com.app.beloz.ui.components.*
import com.app.beloz.ui.viewModel.*
import kotlinx.coroutines.launch

@Composable
fun Cuenta(navController: NavController, authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel, pedidosViewModel: PedidosViewModel, cartViewModel: CartViewModel) {
    val user = authViewModel.user
    val context = LocalContext.current
    val preferences = remember { PerfilSaborRepository(context.applicationContext) }
    val personal = preferences.personalizacionActivaFlow().collectAsState(initial = false).value
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = { BelozTopAppBar("Tu cuenta", navController) },
        bottomBar = { BelozBottomBar(navController, cartViewModel) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Icon(Icons.Default.Person, null, Modifier.size(36.dp), tint = BelozColors.MutedGreen)
            Text(if (user == null) "A tu gusto." else "Bienvenido/a, ${user.name}",
                style = MaterialTheme.typography.headlineMedium, color = BelozColors.Ink)
            if (user == null) {
                Button(onClick = { navController.navigate("InicioSesionUsuario") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Iniciar sesión")
                }
                OutlinedButton(onClick = { navController.navigate("usuario_registro") }, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear cuenta")
                }
            } else {
                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = BelozColors.MutedText)
                listOf(
                    "Correo electrónico" to "modificar_email",
                    "Contraseña" to "modificar_password",
                    "Número de teléfono" to "modificacion_telefono",
                    "Mis pedidos" to "pedidos"
                ).forEach { (label, route) -> AccountRow(label) { navController.navigate(route) } }
            }
            HorizontalDivider(color = BelozColors.Outline)
            Text("Tus preferencias", style = MaterialTheme.typography.titleLarge)
            ListItem(
                headlineContent = { Text("Recordar mis gustos") },
                supportingContent = { Text("Historial local. El chat recibe un resumen de gustos. Al desactivarlo se borra el historial.") },
                trailingContent = { Switch(checked = personal, onCheckedChange = { enabled ->
                    scope.launch {
                        try { preferences.establecerPersonalizacion(enabled) }
                        catch (_: Exception) { snackbar.showSnackbar("No se han podido guardar las preferencias.") }
                    }
                }) },
                colors = ListItemDefaults.colors(containerColor = BelozColors.MintSurface)
            )
            TextButton(enabled = personal, onClick = {
                scope.launch {
                    try {
                        preferences.borrarPerfil()
                        snackbar.showSnackbar("Historial de gustos borrado.")
                    } catch (_: Exception) { snackbar.showSnackbar("No se ha podido borrar el historial.") }
                }
            }) { Text("Borrar historial de gustos") }
            HorizontalDivider(color = BelozColors.Outline)
            AccountRow("Pagos") { navController.navigate("metodo_pago") }
            AccountRow("Privacidad") { navController.navigate("privacidad") }
            if (user != null) {
                OutlinedButton(onClick = {
                    paymentViewModel.clearPaymentData()
                    authViewModel.logout(pedidosViewModel, cartViewModel)
                }, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
                TextButton(onClick = { navController.navigate("eliminarcuenta") }) {
                    Text("Eliminar mi cuenta", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun AccountRow(title: String, action: () -> Unit) {
    ListItem(
        headlineContent = { Text(title) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = BelozColors.MutedGreen) },
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(onClick = action),
        colors = ListItemDefaults.colors(containerColor = BelozColors.MintSurface)
    )
}
