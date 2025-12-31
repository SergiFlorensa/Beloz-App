package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = { Text("Eliminar Cuenta", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF285346))
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF285346))
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "¿Quieres eliminar tu cuenta?",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Eliminar tu cuenta es irreversible. Todos tus datos (pedidos, información personal y métodos de pago) se eliminarán de manera permanente.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Para proceder con la eliminación de la cuenta, por favor confirma.",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (!isUserLoggedIn) {
                    Text(
                        text = "Debes iniciar sesión para poder eliminar tu cuenta.",
                        color = Color(0xFFFFA500),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        showConfirmationDialog = true
                    },
                    enabled = isUserLoggedIn,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isUserLoggedIn) Color(0xFFFFA500) else Color.Gray,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Eliminar Cuenta")
                }
            }
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmationDialog = false
                            authViewModel.deleteAccount(
                                paymentViewModel = paymentViewModel,
                                onSuccess = {
                                    showSuccessDialog = true
                                },
                                onError = { error ->
                                    errorMessage = error
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showConfirmationDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("Cancelar", color = Color.Black)
                    }
                },
                containerColor = Color.White
            )
        }

        // Dialogo de éxito
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
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                    ) {
                        Text("Aceptar")
                    }
                },
                title = { Text("Cuenta Eliminada") },
                text = { Text("Tu cuenta ha sido eliminada exitosamente.") },
                containerColor = Color.White
            )
        }
    }
}
