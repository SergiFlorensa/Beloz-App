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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.M)
@Composable
fun MetodoPago(navController: NavController, paymentViewModel: PaymentViewModel) {
    var nombreTitular by remember { mutableStateOf(TextFieldValue()) }
    var numeroTarjeta by remember { mutableStateOf(TextFieldValue()) }
    var fechaExpiracion by remember { mutableStateOf(TextFieldValue()) }
    var tipoTarjeta by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var isEditable by remember { mutableStateOf(true) }

    val datosBancarios by paymentViewModel::datosBancarios

    LaunchedEffect(Unit) {
        paymentViewModel.loadPaymentData { error -> errorMessage = error }
    }

    LaunchedEffect(datosBancarios) {
        if (datosBancarios != null) {
            val db = datosBancarios!!
            nombreTitular = TextFieldValue(db.nombreTitular ?: "")
            val decryptedNumber = paymentViewModel.decryptCardNumber(
                db.numeroTarjetaEncriptado,
                db.iv
            )
            numeroTarjeta = TextFieldValue(decryptedNumber ?: "")
            fechaExpiracion = TextFieldValue(db.fechaExpiracion ?: "")
            tipoTarjeta = TextFieldValue(db.tipoTarjeta ?: "")
            isEditable = false
        } else {
            nombreTitular = TextFieldValue("")
            numeroTarjeta = TextFieldValue("")
            fechaExpiracion = TextFieldValue("")
            tipoTarjeta = TextFieldValue("")
            isEditable = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Método de Pago", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
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
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tarjeta Bancaria", color = Color.White, fontSize = 18.sp)

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    value = nombreTitular,
                    onValueChange = {
                        var input = it.text.filter { char -> char.isLetter() || char.isWhitespace() }
                        nombreTitular = TextFieldValue(
                            text = input,
                            selection = TextRange(input.length)
                        )
                    },
                    placeholder = "Nombre del Titular",
                    icon = R.drawable.usernombre,
                    enabled = isEditable,
                )


                InputField(
                    value = numeroTarjeta,
                    onValueChange = {
                        var input = it.text.filter { char -> char.isDigit() }

                        if (input.length > 16) input = input.take(16)

                        numeroTarjeta = TextFieldValue(
                            text = input,
                            selection = TextRange(input.length)
                        )
                    },
                    placeholder = "Número de Tarjeta",
                    icon = R.drawable.tarjetauser,
                    singleLine = true,
                    enabled = isEditable,
                )

                InputField(
                    value = fechaExpiracion,
                    onValueChange = {
                        // Tomamos sólo dígitos
                        var input = it.text.filter { char -> char.isDigit() }

                        // Limitamos a 4 dígitos máximo (2 para mes y 2 para año)
                        if (input.length > 4) {
                            input = input.take(4)
                        }

                        // Al tener más de 2 dígitos, insertamos la barra "/"
                        val formatted = when {
                            input.length <= 2 -> input // Si aún no se completaron 2 dígitos, no ponemos la barra
                            else -> {
                                val month = input.take(2)
                                val year = input.drop(2)
                                "$month/$year"
                            }
                        }

                        // Actualizamos el valor del campo con el texto formateado
                        fechaExpiracion = TextFieldValue(
                            text = formatted,
                            selection = TextRange(formatted.length)
                        )
                    },
                    placeholder = "Fecha de Expiración (MM/AA)",
                    icon = R.drawable.calendario,
                    enabled = isEditable,
                )


                InputField(
                    value = tipoTarjeta,
                    onValueChange = { tipoTarjeta = it },
                    placeholder = "Tipo de Tarjeta (Opcional)",
                    icon = R.drawable.tarjetauser,
                    enabled = isEditable,
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) Text(text = errorMessage, color = Color(0xFFFFA500), fontSize = 14.sp)
                if (successMessage.isNotEmpty()) Text(text = successMessage, color = Color.Green, fontSize = 14.sp)

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditable) {
                    Button(
                        onClick = {
                            if (nombreTitular.text.isBlank() || numeroTarjeta.text.isBlank() || fechaExpiracion.text.isBlank()) {
                                errorMessage = "Por favor, complete todos los campos."
                                successMessage = ""
                            } else if (numeroTarjeta.text.length != 16) {
                                errorMessage = "El número de tarjeta debe contener exactamente 16 dígitos."
                                successMessage = ""
                            } else {
                                paymentViewModel.savePaymentData(
                                    nombreTitular = nombreTitular.text,
                                    numeroTarjeta = numeroTarjeta.text,
                                    fechaExpiracion = fechaExpiracion.text,
                                    tipoTarjeta = tipoTarjeta.text,
                                    onSuccess = {
                                        successMessage = "Datos de pago guardados correctamente."
                                        errorMessage = ""
                                        isEditable = false
                                        navController.navigate("home") {
                                            popUpTo("metodo_pago") { inclusive = true }
                                        }
                                    },
                                    onError = { errorMessage = it }
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                    ) {
                        Text("Guardar", color = Color.Black)
                    }
                } else {
                    Button(
                        onClick = {
                            isEditable = true
                            successMessage = ""
                            errorMessage = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                    ) {
                        Text("Modificar", color = Color.Black)
                    }
                }
            }
        }
    }
}
