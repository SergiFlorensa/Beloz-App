package com.app.beloz.ui.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BotonRegistro
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.AuthViewModel
import java.util.regex.Pattern


@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = { Text("Registrar", color = Color.White) },
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
                    .padding(horizontal = 32.dp, vertical = 16.dp),
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
                    onValueChange = { input ->
                        emailController.value = input
                    },
                    placeholder = "Correo Electrónico",
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
                    placeholder = "Contraseña",
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
                    placeholder = "Confirmar Contraseña",
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
                    placeholder = "Número de Teléfono",
                    icon = R.drawable.telefono
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color(0xFFFFA500), fontSize = 14.sp)
                }

                BotonRegistro(
                    onPressed = {
                        val sinEspacios = numTelefonoController.value.text.replace(" ", "")
                        val email = emailController.value.text.trim()

                        if (nameController.value.text.isEmpty() || surnameController.value.text.isEmpty() ||
                            emailController.value.text.isEmpty() || passwordController.value.text.isEmpty() ||
                            confirmPasswordController.value.text.isEmpty() || numTelefonoController.value.text.isEmpty()) {

                            errorMessage = "Por favor, completa todos los campos para poder registrarte."
                            return@BotonRegistro
                        }
                        if (sinEspacios.length != 9) {
                            errorMessage = "El número de teléfono debe tener 9 dígitos."
                            return@BotonRegistro
                        }
                        if (!esEmailValido(emailController.value.text.trim())) {
                            errorMessage = "El correo electrónico no es válido."
                            return@BotonRegistro
                        }
                        if (passwordController.value.text.length < 6) {
                            errorMessage = "La contraseña debe tener al menos 6 caracteres."
                            return@BotonRegistro
                        }
                        if (!emailRegex.matches(email)) {
                            errorMessage = "El formato del correo electrónico no es válido."
                            return@BotonRegistro
                        }
                        val domain = email.substringAfterLast("@")
                        if (domain !in allowedDomains) {
                            errorMessage = "El dominio del correo electrónico no está permitido."
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
                                onSuccess = {
                                    showSuccessDialog = true
                                },
                                onError = { error ->
                                    if (error.contains("Email ya registrado", ignoreCase = true)) {
                                        errorMessage = "El email ya está registrado."
                                    } else {
                                        errorMessage = error
                                    }
                                }
                            )
                        } else {
                            errorMessage = "Las contraseñas no coinciden."
                        }
                    }
                )

                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showSuccessDialog = false
                                    navController.navigate("home")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFFFA500
                                    )
                                )
                            ) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Registro exitoso") },
                        text = { Text("¡Te has registrado correctamente!") },
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}
fun esEmailValido(email: String): Boolean {
    val pattern = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )
    return pattern.matcher(email).matches()
}
