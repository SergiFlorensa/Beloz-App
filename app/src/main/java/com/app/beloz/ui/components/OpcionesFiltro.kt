package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.beloz.theme.DanfordFontFamily

@Composable
fun OpcionesFiltro(
    title: String,
    options: List<Map<String, Any>>,
    selectedOption: Map<String, Any>?,
    onOptionSelected: (Map<String, Any>) -> Unit,
    onShowResults: () -> Unit
) {
    var selected by remember { mutableStateOf(selectedOption) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = DanfordFontFamily,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column {
            options.forEach { option ->
                val label = option["label"].toString()
                val iconResId = option["iconPath"] as? Int
                val isSelected = selected == option

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selected = option
                            onOptionSelected(option)
                        }
                        .padding(vertical = 8.dp)
                ) {
                    if (iconResId != null) {
                        Image(
                            painter = painterResource(id = iconResId),
                            contentDescription = label,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = label,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            selected = option
                            onOptionSelected(option)
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color(0xFFFFA500),
                            unselectedColor = Color.White
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onShowResults,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(40.dp)
            ) {
                Text(
                    text = "Mostrar resultados",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
