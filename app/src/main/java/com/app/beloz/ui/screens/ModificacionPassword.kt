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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificacionPassword(navController: NavController, authViewModel: AuthViewModel) {
    var currentPassword by remember { mutableStateOf(TextFieldValue()) }
    var newPassword by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cambiar Contraseña", color = Color.White) },
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
                //modifier = Modifier.padding(top = 36.dp)
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
                    .padding(top = 6.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InputField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    placeholder = "Contraseña Actual",
                    icon = com.app.beloz.R.drawable.password,
                    isPassword = true
                )

                InputField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = "Nueva Contraseña",
                    icon = com.app.beloz.R.drawable.password,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color(0xFFFFA500), fontSize = 14.sp)
                }
                if (successMessage.isNotEmpty()) {
                    Text(text = successMessage, color = Color.Green, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp) // Espacio entre botones
                    ) {
                        Text("Cancelar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            authViewModel.updatePassword(
                                currentPassword = currentPassword.text.trim(),
                                newPassword = newPassword.text.trim(),
                                onSuccess = {
                                    successMessage = "Contraseña actualizada exitosamente."
                                    errorMessage = ""
                                },
                                onError = { error ->
                                    errorMessage = error
                                    successMessage = ""
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
                    ) {
                        Text("Guardar", color = Color.Black)
                    }
                }
            }
        }
    }
}
