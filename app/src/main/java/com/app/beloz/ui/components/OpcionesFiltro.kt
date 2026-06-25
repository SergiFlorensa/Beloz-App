package com.app.beloz.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OpcionesFiltro(
    title: String,
    options: List<Map<String, Any>>,
    selectedOption: Map<String, Any>?,
    onOptionSelected: (Map<String, Any>) -> Unit,
    onShowResults: () -> Unit
) {
    var selected by remember(selectedOption) { mutableStateOf(selectedOption) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF10231E),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = if (title == "Precio") "Ajusta el pedido a tu presupuesto" else "Ordena lo que ves primero",
                color = Color(0xFFCFE8DC),
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                options.forEach { option ->
                    val label = option["label"].toString()
                    val iconResId = option["iconPath"] as? Int
                    val isSelected = selected == option

                    FilterOptionRow(
                        label = label,
                        iconResId = iconResId,
                        selected = isSelected,
                        onClick = {
                            selected = option
                            onOptionSelected(option)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onShowResults,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF56E39F),
                    contentColor = Color(0xFF10231E)
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = "Ver resultados",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun FilterOptionRow(
    label: String,
    iconResId: Int?,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Color(0xFFF3FBF6) else Color(0x14FFFFFF),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color(0xFFFFB24D) else Color(0x12FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(14.dp),
                    color = if (selected) Color(0xFFFFB24D) else Color(0x1AFFFFFF)
                ) {}
                if (iconResId != null) {
                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = label,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = label.take(1),
                        color = if (selected) Color(0xFF10231E) else Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = label,
                color = if (selected) Color(0xFF10231E) else Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Surface(
                shape = CircleShape,
                color = if (selected) Color(0xFF56E39F) else Color(0x29FFFFFF),
                modifier = Modifier.size(18.dp)
            ) {}
        }
    }
}
