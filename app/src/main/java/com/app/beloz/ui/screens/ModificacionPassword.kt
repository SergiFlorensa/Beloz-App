package com.app.beloz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.app.beloz.ui.viewModel.AuthViewModel

@Composable
fun ModificacionPassword(navController: NavController, authViewModel: AuthViewModel) {
    var currentPassword by remember { mutableStateOf(TextFieldValue()) }
    var newPassword by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            BelozTopAppBar(
                title = "Contrasena",
                subtitle = "Refuerza la seguridad",
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BelozColors.MintSurface)
                .padding(paddingValues)
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
                        text = "Cambia tu clave",
                        color = BelozColors.Ink,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    InputField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        placeholder = "Contrasena actual",
                        icon = R.drawable.password,
                        isPassword = true
                    )
                    InputField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        placeholder = "Nueva contrasena",
                        icon = R.drawable.password,
                        isPassword = true
                    )

                    StatusText(errorMessage = errorMessage, successMessage = successMessage)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Cancelar", color = BelozColors.Ink, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                authViewModel.updatePassword(
                                    currentPassword = currentPassword.text.trim(),
                                    newPassword = newPassword.text.trim(),
                                    onSuccess = {
                                        successMessage = "Contrasena actualizada correctamente."
                                        errorMessage = ""
                                    },
                                    onError = { error ->
                                        errorMessage = error
                                        successMessage = ""
                                    }
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BelozColors.Green,
                                contentColor = BelozColors.Ink
                            )
                        ) {
                            Text("Guardar", fontWeight = FontWeight.Black)
                        }
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
