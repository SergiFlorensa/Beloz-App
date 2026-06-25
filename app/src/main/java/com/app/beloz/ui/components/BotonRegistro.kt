package com.app.beloz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun BotonRegistro(
    onPressed: (() -> Unit)?,
    enabled: Boolean = true
) {
    Button(
        onClick = onPressed ?: {},
        colors = ButtonDefaults.buttonColors(
            containerColor = BelozColors.Green,
            contentColor = BelozColors.Ink
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        enabled = enabled
    ) {
        Text(
            text = "Registrar",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = DanfordFontFamily
        )
    }
}
