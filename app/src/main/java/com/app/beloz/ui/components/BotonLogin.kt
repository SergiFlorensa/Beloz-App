package com.app.beloz.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun BotonLogin(
    onPressed: () -> Unit,
    text: String,
    enabled: Boolean = true
) {
    Button(
        onClick = onPressed,
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
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = DanfordFontFamily
        )
    }
}
