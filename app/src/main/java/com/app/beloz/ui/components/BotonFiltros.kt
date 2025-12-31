package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.beloz.R
import com.app.beloz.theme.DanfordFontFamily  // Asegúrate de importar la fuente personalizada

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotonFiltros(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF285346),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontFamily = DanfordFontFamily
            )
            Spacer(modifier = Modifier.size(8.dp))
            Image(
                painter = painterResource(id = R.drawable.dropdown),
                contentDescription = "Arrow down",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
