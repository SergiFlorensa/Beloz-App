package com.app.beloz.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.PaymentViewModel

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
            BelozTopAppBar(
                title = "Metodo de pago",
                subtitle = "Tarjeta y datos bancarios",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tarjeta bancaria",
                        color = BelozColors.Ink,
                        fontSize = 22.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (isEditable) "Introduce tus datos de pago" else "Datos guardados",
                        color = BelozColors.MutedText,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                    )

                    InputField(
                        value = nombreTitular,
                        onValueChange = {
                            val input = it.text.filter { char -> char.isLetter() || char.isWhitespace() }
                            nombreTitular = TextFieldValue(
                                text = input,
                                selection = TextRange(input.length)
                            )
                        },
                        placeholder = "Nombre del titular",
                        icon = R.drawable.usernombre,
                        enabled = isEditable
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
                        placeholder = "Numero de tarjeta",
                        icon = R.drawable.tarjetauser,
                        singleLine = true,
                        enabled = isEditable
                    )

                    InputField(
                        value = fechaExpiracion,
                        onValueChange = {
                            var input = it.text.filter { char -> char.isDigit() }
                            if (input.length > 4) input = input.take(4)
                            val formatted = when {
                                input.length <= 2 -> input
                                else -> "${input.take(2)}/${input.drop(2)}"
                            }
                            fechaExpiracion = TextFieldValue(
                                text = formatted,
                                selection = TextRange(formatted.length)
                            )
                        },
                        placeholder = "Expiracion MM/AA",
                        icon = R.drawable.calendario,
                        enabled = isEditable
                    )

                    InputField(
                        value = tipoTarjeta,
                        onValueChange = { tipoTarjeta = it },
                        placeholder = "Tipo de tarjeta opcional",
                        icon = R.drawable.tarjetauser,
                        enabled = isEditable
                    )

                    StatusText(errorMessage = errorMessage, successMessage = successMessage)

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isEditable) {
                                if (nombreTitular.text.isBlank() || numeroTarjeta.text.isBlank() || fechaExpiracion.text.isBlank()) {
                                    errorMessage = "Completa los campos obligatorios."
                                    successMessage = ""
                                } else if (numeroTarjeta.text.length != 16) {
                                    errorMessage = "La tarjeta debe contener 16 digitos."
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
                                        onError = { error -> errorMessage = error }
                                    )
                                }
                            } else {
                                isEditable = true
                                successMessage = ""
                                errorMessage = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BelozColors.Green,
                            contentColor = BelozColors.Ink
                        ),
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(if (isEditable) "Guardar" else "Modificar", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusText(errorMessage: String, successMessage: String) {
    if (errorMessage.isNotEmpty()) {
        Text(text = errorMessage, color = Color(0xFFE24D4D), fontSize = 13.sp)
    }
    if (successMessage.isNotEmpty()) {
        Text(text = successMessage, color = BelozColors.MutedGreen, fontSize = 13.sp)
    }
}
