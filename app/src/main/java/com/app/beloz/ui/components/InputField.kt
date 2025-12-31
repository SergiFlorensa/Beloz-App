package com.app.beloz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun InputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    icon: Int,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    textSize: Float = 14f,
    enabled: Boolean = true,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = if (enabled) Color(0xFFB0BEC5) else Color.Gray.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = DanfordFontFamily
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = if (enabled) Color.White else Color.Gray.copy(alpha = 0.2f),
            unfocusedContainerColor = if (enabled) Color.White else Color.Gray.copy(alpha = 0.2f),
            focusedTextColor = if (enabled) Color.Black else Color.Gray,
            unfocusedTextColor = if (enabled) Color.Black else Color.Gray
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = if (enabled) Color(0xFFB0BEC5) else Color.Gray.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier
            .padding(vertical = 8.dp)
            .widthIn(max = 300.dp)
            .border(BorderStroke(1.dp, Color(0xFFE0E0E0)), shape = RoundedCornerShape(12.dp))
            .height(60.dp),
        singleLine = singleLine,
        textStyle = TextStyle(fontSize = textSize.sp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp)
    )
}
