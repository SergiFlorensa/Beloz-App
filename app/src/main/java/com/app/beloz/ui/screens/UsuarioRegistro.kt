package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.BotonRegistro
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.AuthViewModel
import java.util.regex.Pattern

@Composable
fun UsuarioRegistro(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val nameController = remember { mutableStateOf(TextFieldValue()) }
    val surnameController = remember { mutableStateOf(TextFieldValue()) }
    val emailController = remember { mutableStateOf(TextFieldValue()) }
    val passwordController = remember { mutableStateOf(TextFieldValue()) }
    val confirmPasswordController = remember { mutableStateOf(TextFieldValue()) }
    val numTelefonoController = remember { mutableStateOf(TextFieldValue()) }

    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    val allowedDomains = listOf("gmail.com", "hotmail.com", "hotmail.es", "yahoo.com")

    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Crear cuenta",
                subtitle = "Tus datos para pedir mas rapido",
                navController = navController
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputField(
                    value = nameController.value,
                    onValueChange = { input ->
                        val filteredText = input.text.filter { it.isLetter() || it == ' ' || it == '-' }
                        nameController.value = TextFieldValue(
                            text = filteredText,
                            selection = TextRange(filteredText.length)
                        )
                    },
                    placeholder = "Nombre",
                    icon = R.drawable.usernombre
                )
                InputField(
                    value = surnameController.value,
                    onValueChange = { input ->
                        val filteredText = input.text.filter { it.isLetter() || it == ' ' || it == '-' }
                        surnameController.value = TextFieldValue(
                            text = filteredText,
                            selection = TextRange(filteredText.length)
                        )
                    },
                    placeholder = "Apellido",
                    icon = R.drawable.userapellido
                )
                InputField(
                    value = emailController.value,
                    onValueChange = { input -> emailController.value = input },
                    placeholder = "Correo electronico",
                    icon = R.drawable.email
                )
                InputField(
                    value = passwordController.value,
                    onValueChange = { input ->
                        val filteredText = input.text.filter { it != ' ' }
                        passwordController.value = TextFieldValue(
                            text = filteredText,
                            selection = TextRange(filteredText.length)
                        )
                    },
                    placeholder = "Contrasena",
                    icon = R.drawable.password,
                    isPassword = true
                )
                InputField(
                    value = confirmPasswordController.value,
                    onValueChange = { input ->
                        val filteredText = input.text.filter { it != ' ' }
                        confirmPasswordController.value = TextFieldValue(
                            text = filteredText,
                            selection = TextRange(filteredText.length)
                        )
                    },
                    placeholder = "Confirmar contrasena",
                    icon = R.drawable.password,
                    isPassword = true
                )
                InputField(
                    value = numTelefonoController.value,
                    onValueChange = { input ->
                        val filteredText = input.text.filter { it.isDigit() }.take(9)
                        numTelefonoController.value = TextFieldValue(
                            text = filteredText,
                            selection = TextRange(filteredText.length)
                        )
                    },
                    placeholder = "Numero de telefono",
                    icon = R.drawable.telefono
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = BelozColors.Orange, fontSize = 14.sp)
                }

                BotonRegistro(
                    onPressed = {
                        val sinEspacios = numTelefonoController.value.text.replace(" ", "")
                        val email = emailController.value.text.trim()

                        if (
                            nameController.value.text.isEmpty() ||
                            surnameController.value.text.isEmpty() ||
                            emailController.value.text.isEmpty() ||
                            passwordController.value.text.isEmpty() ||
                            confirmPasswordController.value.text.isEmpty() ||
                            numTelefonoController.value.text.isEmpty()
                        ) {
                            errorMessage = "Completa todos los campos para registrarte."
                            return@BotonRegistro
                        }
                        if (sinEspacios.length != 9) {
                            errorMessage = "El numero de telefono debe tener 9 digitos."
                            return@BotonRegistro
                        }
                        if (!esEmailValido(emailController.value.text.trim())) {
                            errorMessage = "El correo electronico no es valido."
                            return@BotonRegistro
                        }
                        if (passwordController.value.text.length < 6) {
                            errorMessage = "La contrasena debe tener al menos 6 caracteres."
                            return@BotonRegistro
                        }
                        if (!emailRegex.matches(email)) {
                            errorMessage = "El formato del correo electronico no es valido."
                            return@BotonRegistro
                        }
                        val domain = email.substringAfterLast("@")
                        if (domain !in allowedDomains) {
                            errorMessage = "El dominio del correo electronico no esta permitido."
                            return@BotonRegistro
                        }
                        if (passwordController.value.text == confirmPasswordController.value.text) {
                            errorMessage = ""
                            authViewModel.register(
                                name = nameController.value.text,
                                surname = surnameController.value.text,
                                email = emailController.value.text.trim(),
                                password = passwordController.value.text,
                                numTelefono = numTelefonoController.value.text,
                                onSuccess = { showSuccessDialog = true },
                                onError = { error ->
                                    errorMessage = if (error.contains("Email ya registrado", ignoreCase = true)) {
                                        "El email ya esta registrado."
                                    } else {
                                        error
                                    }
                                }
                            )
                        } else {
                            errorMessage = "Las contrasenas no coinciden."
                        }
                    }
                )
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            navController.navigate("home")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BelozColors.Green)
                    ) {
                        Text("Aceptar", color = BelozColors.Ink)
                    }
                },
                title = { Text("Registro exitoso", color = BelozColors.Ink) },
                text = { Text("Te has registrado correctamente.", color = BelozColors.MutedText) },
                containerColor = Color.White
            )
        }
    }
}

fun esEmailValido(email: String): Boolean {
    val pattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    return pattern.matcher(email).matches()
}
