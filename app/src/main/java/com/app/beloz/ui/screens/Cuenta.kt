package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel
import com.app.beloz.ui.viewModel.PedidosViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class AccountAction(
    val title: String,
    val subtitle: String,
    val icon: Int,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun Cuenta(
    navController: NavController,
    authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel,
    pedidosViewModel: PedidosViewModel,
    cartViewModel: CartViewModel
) {
    val user = authViewModel.user
    val displayName = listOfNotNull(user?.name, user?.surname)
        .joinToString(" ")
        .ifBlank { "Invitado" }
    val email = user?.email ?: "Inicia sesion para sincronizar tus datos"
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var showLogoutSnackbar by remember { mutableStateOf(false) }

    val accountActions = listOf(
        AccountAction("Correo electronico", "Actualiza tu email de acceso", R.drawable.email, "modificar_email"),
        AccountAction("Contrasena", "Cambia tu clave de seguridad", R.drawable.password, "modificar_password"),
        AccountAction("Telefono", "Gestiona tu numero de contacto", R.drawable.telefono, "modificacion_telefono"),
        AccountAction("Metodos de pago", "Tarjetas y formas de pago", R.drawable.metodopago, "metodo_pago"),
        AccountAction("Pedidos", "Historial y seguimiento", R.drawable.informacion, "pedidos"),
        AccountAction("Privacidad", "Permisos y datos personales", R.drawable.privacidad, "privacidad")
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            BelozTopAppBar(title = "Cuenta", navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AccountHero(
                displayName = displayName,
                email = email,
                loggedIn = user != null
            )

            Text(
                text = "Gestion personal",
                color = Color(0xFF10231E),
                fontFamily = FontFamily.SansSerif,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 4.dp)
            )

            accountActions.forEach { action ->
                AccountActionItem(
                    action = action,
                    onClick = { navController.navigate(action.route) }
                )
            }

            if (user == null) {
                Button(
                    onClick = { navController.navigate("InicioSesionUsuario") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10231E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Iniciar sesion", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { navController.navigate("usuario_registro") },
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                Text("Crear cuenta", color = BelozColors.Ink, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = {
                        paymentViewModel.clearPaymentData()
                        authViewModel.logout(pedidosViewModel, cartViewModel)
                        showLogoutSnackbar = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE24D4D),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesion",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Cerrar sesion", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showLogoutSnackbar) {
            LaunchedEffect(snackbarHostState) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Sesion desconectada",
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

@Composable
private fun AccountHero(
    displayName: String,
    email: String,
    loggedIn: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = BelozColors.Ink,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            BelozColors.Ink,
                            BelozColors.MutedGreen
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(94.dp)
                    .clip(CircleShape)
                    .background(Color(0x224DE59B))
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = BelozColors.SoftMint,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = R.drawable.tarjetauser),
                            contentDescription = "Usuario",
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (loggedIn) "Bienvenido/a" else "Tu cuenta Beloz",
                        color = Color(0xFFCFE8DC),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = displayName,
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = email,
                        color = Color(0xFFE8F2EC),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountActionItem(
    action: AccountAction,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = BelozColors.Card,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BelozColors.SoftMint,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = action.icon),
                    contentDescription = action.title,
                    tint = BelozColors.MutedGreen,
                    modifier = Modifier.size(25.dp)
                )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    color = BelozColors.Ink,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = action.subtitle,
                    color = BelozColors.MutedText,
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = BelozColors.MutedGreen,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
