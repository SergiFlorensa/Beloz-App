package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward // Importar el icono de flecha hacia adelante
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.res.painterResource

@Composable
fun EstructuraCuentaItems(
    icon: Int,
    label: String,
    onTap: () -> Unit,
    isEditable: Boolean = false,
    initialValue: String? = null,
    onChanged: ((String) -> Unit)? = null
) {
    var textState by remember { mutableStateOf(TextFieldValue(initialValue ?: "")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTap() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (isEditable) {
            TextField(
                value = textState,
                onValueChange = { value ->
                    textState = value
                    onChanged?.invoke(value.text)
                },
                placeholder = { Text(label, color = Color.White.copy(alpha = 0.7f)) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                modifier = Modifier.weight(1f)
            )
        } else {
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}
