package com.app.beloz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.beloz.R
import com.app.beloz.ui.components.BotonLogin
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioSesionUsuario(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val emailController = remember { mutableStateOf(TextFieldValue()) }
    val passwordController = remember { mutableStateOf(TextFieldValue()) }

    var errorMessage by remember { mutableStateOf("") }
    var showLoginSuccessDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showForgotPasswordSuccessDialog by remember { mutableStateOf(false) }
    val forgotPasswordEmailController = remember { mutableStateOf(TextFieldValue()) }
    var forgotPasswordError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión", color = Color.White) },
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
                    .padding(start = 32.dp, end = 32.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iconoapp),
                    contentDescription = "Logo",
                    modifier = Modifier.size(160.dp)
                )

                Text(
                    text = "¡Hola, Bienvenido!",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )

                Text(
                    text = "Inicia sesión para continuar",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    value = emailController.value,
                    onValueChange = { emailController.value = it },
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


                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color(0xFFFFA500), fontSize = 14.sp)
                }

                BotonLogin(
                    onPressed = {
                        val email = emailController.value.text.trim()
                        val password = passwordController.value.text
                        authViewModel.login(
                            email = email,
                            password = password,
                            onSuccess = {
                                errorMessage = ""
                                showLoginSuccessDialog = true
                            },
                            onError = { error -> errorMessage = error }
                        )
                    },
                    text = "Iniciar Sesión"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = Color(0xFFFFA500),
                    modifier = Modifier.clickable { showForgotPasswordDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = androidx.compose.ui.text.SpanStyle(color = Color.White)) {
                            append("¿No tienes cuenta? ")
                        }

                        withStyle(style = androidx.compose.ui.text.SpanStyle(textDecoration = TextDecoration.Underline, color = Color(0xFFFFA500))) {
                            append("Regístrate aquí.")
                        }
                    },
                    modifier = Modifier.clickable { navController.navigate("usuario_registro") }
                )

                if (showForgotPasswordDialog) {
                    AlertDialog(
                        onDismissRequest = { showForgotPasswordDialog = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    if (forgotPasswordEmailController.value.text.isNotEmpty() &&
                                        android.util.Patterns.EMAIL_ADDRESS.matcher(forgotPasswordEmailController.value.text).matches()
                                    ) {
                                        showForgotPasswordDialog = false
                                        forgotPasswordError = ""
                                        showForgotPasswordSuccessDialog = true
                                    } else {
                                        forgotPasswordError = "Correo electrónico incorrecto"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                            ) {
                                Text("Enviar", color = Color.White)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showForgotPasswordDialog = false }) {
                                Text("Cancelar", color = Color(0xFFFFA500))
                            }
                        },
                        title = { Text("Restablecer Contraseña", color = Color.Black) },
                        text = {
                            Column {
                                Text(
                                    text = "Introduce tu correo electrónico para restablecer tu contraseña.",
                                    color = Color.Black,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = forgotPasswordEmailController.value,
                                    onValueChange = { forgotPasswordEmailController.value = it },
                                    label = { Text("Correo Electrónico") },
                                    singleLine = true,
                                    isError = forgotPasswordError.isNotEmpty()
                                )
                                if (forgotPasswordError.isNotEmpty()) {
                                    Text(text = forgotPasswordError, color = Color.Red, fontSize = 14.sp)
                                }
                            }
                        },
                        containerColor = Color.White
                    )
                }

                if (showForgotPasswordSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showForgotPasswordSuccessDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showForgotPasswordSuccessDialog = false }) {
                                Text("Aceptar", color = Color(0xFFFFA500))
                            }
                        },
                        title = { Text("Restablecimiento de Contraseña", color = Color.Black) },
                        text = { Text("Compruebe su correo electrónico para modificar el password", color = Color.Gray) },
                        containerColor = Color.White
                    )
                }

                if (showLoginSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showLoginSuccessDialog = false },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showLoginSuccessDialog = false
                                    navController.navigate("home") {
                                        popUpTo("InicioSesionUsuario") { inclusive = true }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                            ) {
                                Text("Continuar", color = Color.White)
                            }
                        },
                        title = {
                            Text(
                                text = "¡Bienvenido!",
                                color = Color.Black,
                                fontSize = 20.sp
                            )
                        },
                        text = {
                            Text(
                                text = "Has iniciado sesión correctamente.",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        },
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}
