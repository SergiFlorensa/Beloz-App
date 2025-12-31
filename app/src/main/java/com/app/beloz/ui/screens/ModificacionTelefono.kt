package com.app.beloz.ui.screens

import android.util.Log
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
import com.app.beloz.R
import com.app.beloz.ui.components.InputField
import com.app.beloz.ui.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificacionTelefono(navController: NavController, authViewModel: AuthViewModel) {
    val currentPhoneNumber = authViewModel.user?.numTelefono ?: ""
    Log.d("ModificacionTelefono", "Current phone number: $currentPhoneNumber")

    var newPhoneNumber by remember { mutableStateOf(TextFieldValue(currentPhoneNumber)) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modificar Número de Teléfono", color = Color.White) },
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
                modifier = Modifier.padding(top = 16.dp)
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
                    .padding(horizontal = 16.dp)
                    .align(Alignment.TopCenter),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Número Actual: ",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = currentPhoneNumber,
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                InputField(
                    value = newPhoneNumber,
                    onValueChange = {
                        if (it.text.length <= 9) {
                            newPhoneNumber = it
                        }
                    },
                    placeholder = "Nuevo Número de Teléfono",
                    icon = R.drawable.telefono,
                    enabled = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color(0xFFFFA500), fontSize = 14.sp)
                }
                if (successMessage.isNotEmpty()) {
                    Text(text = successMessage, color = Color.Green, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancelar", color = Color.White)
                    }

                    Button(
                        onClick = {
                            val trimmedPhoneNumber = newPhoneNumber.text.trim()
                            if (trimmedPhoneNumber.isBlank()) {
                                errorMessage = "El número de teléfono no puede estar vacío."
                                successMessage = ""
                            } else if (trimmedPhoneNumber.length != 9 || !trimmedPhoneNumber.all { it.isDigit() }) {
                                errorMessage = "El número de teléfono debe tener 9 dígitos y solo contener números."
                                successMessage = ""
                            } else {
                                authViewModel.updatePhoneNumber(
                                    numTelefono = trimmedPhoneNumber,
                                    onSuccess = {
                                        successMessage = "Número de teléfono actualizado exitosamente."
                                        errorMessage = ""
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        successMessage = ""
                                    }
                                )
                            }
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
