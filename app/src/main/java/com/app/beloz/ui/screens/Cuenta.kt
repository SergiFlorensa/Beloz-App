package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BotonLogin
import com.app.beloz.ui.components.BotonRegistro
import com.app.beloz.ui.components.EstructuraCuentaItems
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel
import com.app.beloz.ui.viewModel.PedidosViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun Cuenta(navController: NavController, authViewModel: AuthViewModel, paymentViewModel: PaymentViewModel, pedidosViewModel: PedidosViewModel, cartViewModel: CartViewModel
) {
    val userName = authViewModel.user?.name ?: ""
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showLogoutSnackbar by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            Surface(
                color = Color(0xFF285346),
                modifier = Modifier.fillMaxWidth()
            ) {
                TopAppBar(
                    title = { Text("Cuenta", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    //modifier = Modifier.padding(top = 36.dp)
                )
            }
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
                    text = "Bienvenido/a, $userName",
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                EstructuraCuentaItems(
                    icon = R.drawable.email,
                    label = "Correo Electrónico",
                    onTap = { navController.navigate("modificar_email") },
                    isEditable = false
                )
                EstructuraCuentaItems(
                    icon = R.drawable.password,
                    label = "Cambiar Contraseña",
                    onTap = { navController.navigate("modificar_password") },
                )
                EstructuraCuentaItems(
                    icon = R.drawable.telefono,
                    label = "Cambiar Número de Teléfono",
                    onTap = { navController.navigate("modificacion_telefono") }
                )
                EstructuraCuentaItems(
                    icon = R.drawable.metodopago,
                    label = "Métodos de Pago",
                    onTap = { navController.navigate("metodo_pago") }
                )
                EstructuraCuentaItems(
                    icon = R.drawable.informacion,
                    label = "Información sobre Pedidos",
                    onTap = {navController.navigate("pedidos")}
                )
                EstructuraCuentaItems(
                    icon = R.drawable.privacidad,
                    label = "Gestionar Privacidad",
                    onTap = {navController.navigate("privacidad")}
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BotonLogin(
                            onPressed = {
                                if (authViewModel.user == null) {
                                    navController.navigate("InicioSesionUsuario")
                                }
                            },
                            text = "Iniciar Sesión",
                            enabled = authViewModel.user == null
                        )

                        BotonRegistro(
                            onPressed = { navController.navigate("usuario_registro") },
                            enabled = authViewModel.user == null
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                paymentViewModel.clearPaymentData()
                                authViewModel.logout(pedidosViewModel,cartViewModel)
                                showLogoutSnackbar = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            enabled = authViewModel.user != null
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Desconectar",
                                modifier = Modifier.size(20.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            if (showLogoutSnackbar) {
                LaunchedEffect(snackbarHostState) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Sesión desconectada",
                            duration = SnackbarDuration.Short
                        )
                        delay(10)
                        showLogoutSnackbar = false
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
