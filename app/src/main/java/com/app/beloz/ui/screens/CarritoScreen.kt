package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.app.beloz.ui.viewModel.CartViewModel
import com.app.beloz.ui.viewModel.AuthViewModel
import com.app.beloz.ui.viewModel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    cartViewModel: CartViewModel,
    authViewModel: AuthViewModel,
    paymentViewModel: PaymentViewModel
) {
    val carrito by cartViewModel.carrito.collectAsState()
    val totalPrice = cartViewModel.getTotalPrice()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val perfilRepo = remember { PerfilSaborRepository(context) }
    val scope = rememberCoroutineScope()

    cartViewModel.currentUser = authViewModel.user
    LaunchedEffect(carrito) {
        cartViewModel.getTotalPrice()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    if (carrito.isNotEmpty()) {
                        IconButton(onClick = { cartViewModel.clearCart() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Vaciar Carrito", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF285346), titleContentColor = Color.White),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF2F2F7))
        ) {
            if (carrito.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF285346))
                ) {
                    items(carrito) { (plato, cantidad) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = MaterialTheme.shapes.medium,
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(plato.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Cantidad: $cantidad", color = Color.Gray)
                                }
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        "€${String.format("%.2f", plato.price * cantidad)}",
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(
                                        onClick = {
                                            cartViewModel.removeFromCart(plato)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar Plato",
                                            tint = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF285346))
                        .padding(16.dp)
                ) {

                    Text(
                        text = "Total: €${String.format("%.2f", totalPrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.End),
                        color = Color.White
                    )

                    Button(
                        onClick = {
                            if (paymentViewModel.datosBancarios != null) {
                                showConfirmDialog = true
                            } else {
                                errorMessage = "Por favor, complete sus datos bancarios antes de confirmar la compra."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                    ) {
                        Text("Confirmar Compra", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        val itemCount = carrito.sumOf { it.second }
                        scope.launch {
                            perfilRepo.registrarEvento(
                                EventoUso(
                                    tipo = TipoEvento.CHECKOUT,
                                    metadata = mapOf(
                                        "total_price" to totalPrice.toString(),
                                        "item_count" to itemCount.toString()
                                    )
                                )
                            )
                        }
                        cartViewModel.confirmPurchase(
                            onSuccess = {
                                showConfirmDialog = false
                                scope.launch {
                                    perfilRepo.registrarEvento(
                                        EventoUso(
                                            tipo = TipoEvento.PURCHASE,
                                            metadata = mapOf(
                                                "total_price" to totalPrice.toString(),
                                                "item_count" to itemCount.toString()
                                            )
                                        )
                                    )
                                }
                                showSuccessDialog = true
                            },
                            onError = { error ->
                                showConfirmDialog = false
                                errorMessage = error
                                showSuccessDialog = false
                            }
                        )
                    }) {
                        Text("Confirmar", color = Color(0xFF285346))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar", color = Color(0xFF285346))
                    }
                },
                title = { Text("Confirmar Compra", color = Color(0xFF285346)) },
                text = {
                    Text(
                        "¿Estás seguro de que deseas confirmar la compra por un total de €${
                            String.format(
                                "%.2f",
                                totalPrice
                            )
                        }?", color = Color.Gray
                    )
                }
            )
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                confirmButton = {
                    TextButton(onClick = { showSuccessDialog = false }) {
                        Text("Aceptar", color = Color(0xFF285346))
                    }
                },
                title = { Text("Compra Realizada", color = Color(0xFF285346)) },
                text = { Text("Tu compra ha sido realizada con éxito.", color = Color.Gray) }
            )
        }

        if (errorMessage.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = { errorMessage = "" },
                confirmButton = {
                    TextButton(onClick = { errorMessage = "" }) {
                        Text("Aceptar", color = Color(0xFF285346))
                    }
                },
                title = { Text("Error", color = Color(0xFF285346)) },
                text = { Text(errorMessage, color = Color.Gray) }
            )
        }
    }
}
