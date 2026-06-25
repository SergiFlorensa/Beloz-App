package com.app.beloz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
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
import com.app.beloz.ui.components.BelozColors
import com.app.beloz.ui.components.BelozTopAppBar
import com.app.beloz.ui.components.BotonLogin
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.AuthViewModel

@Composable
fun InicioSesionUsuario(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val emailController = remember { mutableStateOf(TextFieldValue()) }
    val passwordController = remember { mutableStateOf(TextFieldValue()) }
    val forgotPasswordEmailController = remember { mutableStateOf(TextFieldValue()) }

    var errorMessage by remember { mutableStateOf("") }
    var forgotPasswordError by remember { mutableStateOf("") }
    var showLoginSuccessDialog by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showForgotPasswordSuccessDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = BelozColors.MintSurface,
        topBar = {
            BelozTopAppBar(
                title = "Iniciar sesion",
                subtitle = "Accede a tus pedidos",
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
                    .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iconoapp),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp)
                )

                Text(
                    text = "Hola, bienvenido",
                    color = BelozColors.Ink,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
                Text(
                    text = "Inicia sesion para continuar",
                    color = BelozColors.MutedText,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    value = emailController.value,
                    onValueChange = { emailController.value = it },
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

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = BelozColors.Orange, fontSize = 14.sp)
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
                    text = "Iniciar sesion"
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Olvidaste tu contrasena?",
                    color = BelozColors.MutedGreen,
                    modifier = Modifier.clickable { showForgotPasswordDialog = true }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = BelozColors.Ink)) {
                            append("No tienes cuenta? ")
                        }
                        withStyle(
                            style = SpanStyle(
                                textDecoration = TextDecoration.Underline,
                                color = BelozColors.MutedGreen
                            )
                        ) {
                            append("Registrate aqui.")
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
                                    if (
                                        forgotPasswordEmailController.value.text.isNotEmpty() &&
                                        android.util.Patterns.EMAIL_ADDRESS
                                            .matcher(forgotPasswordEmailController.value.text)
                                            .matches()
                                    ) {
                                        showForgotPasswordDialog = false
                                        forgotPasswordError = ""
                                        showForgotPasswordSuccessDialog = true
                                    } else {
                                        forgotPasswordError = "Correo electronico incorrecto"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BelozColors.Green)
                            ) {
                                Text("Enviar", color = BelozColors.Ink)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showForgotPasswordDialog = false }) {
                                Text("Cancelar", color = BelozColors.Ink)
                            }
                        },
                        title = { Text("Restablecer contrasena", color = BelozColors.Ink) },
                        text = {
                            Column {
                                Text(
                                    text = "Introduce tu correo electronico para restablecer tu contrasena.",
                                    color = BelozColors.Ink,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = forgotPasswordEmailController.value,
                                    onValueChange = { forgotPasswordEmailController.value = it },
                                    label = { Text("Correo electronico") },
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
                                Text("Aceptar", color = BelozColors.Ink)
                            }
                        },
                        title = { Text("Restablecimiento de contrasena", color = BelozColors.Ink) },
                        text = {
                            Text(
                                "Comprueba tu correo electronico para modificar la contrasena.",
                                color = BelozColors.MutedText
                            )
                        },
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
                                colors = ButtonDefaults.buttonColors(containerColor = BelozColors.Green)
                            ) {
                                Text("Continuar", color = BelozColors.Ink)
                            }
                        },
                        title = { Text(text = "Bienvenido", color = BelozColors.Ink, fontSize = 20.sp) },
                        text = { Text(text = "Has iniciado sesion correctamente.", color = BelozColors.MutedText) },
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}
