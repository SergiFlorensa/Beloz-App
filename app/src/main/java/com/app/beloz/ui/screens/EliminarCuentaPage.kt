package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel

private val DangerRed = Color(0xFFE94949)

@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun EliminarCuentaPage(
    navController: NavController,
    authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel
) {
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val isUserLoggedIn = authViewModel.user != null

    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Eliminar cuenta",
                subtitle = "Accion irreversible",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = BelozColors.Card,
                shadowElevation = 3.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Antes de continuar",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = BelozColors.Ink
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Eliminar tu cuenta borrara pedidos, informacion personal y metodos de pago asociados. Esta accion no se puede deshacer.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BelozColors.MutedText,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    if (!isUserLoggedIn) {
                        Text(
                            text = "Debes iniciar sesion para poder eliminar tu cuenta.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BelozColors.Orange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DangerRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = { showConfirmationDialog = true },
                        enabled = isUserLoggedIn,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed,
                            contentColor = Color.White,
                            disabledContainerColor = BelozColors.SoftMint,
                            disabledContentColor = BelozColors.MutedText
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar cuenta", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirmar eliminacion", color = BelozColors.Ink) },
                text = { Text("Seguro que deseas eliminar tu cuenta? Esta accion no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmationDialog = false
                            authViewModel.deleteAccount(
                                paymentViewModel = paymentViewModel,
                                onSuccess = { showSuccessDialog = true },
                                onError = { error -> errorMessage = error }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showConfirmationDialog = false }) {
                        Text("Cancelar", color = BelozColors.Ink)
                    }
                },
                containerColor = Color.White
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BelozColors.Green)
                    ) {
                        Text("Aceptar", color = BelozColors.Ink, fontWeight = FontWeight.Black)
                    }
                },
                title = { Text("Cuenta eliminada", color = BelozColors.Ink) },
                text = { Text("Tu cuenta se ha eliminado correctamente.") },
                containerColor = Color.White
            )
        }
    }
}
