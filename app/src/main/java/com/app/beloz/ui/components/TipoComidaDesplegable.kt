package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.beloz.R

@Composable
fun TipoComidaDesplegable(
    options: List<Map<String, Any>>,
    selectedOptions: List<String>,
    onOptionSelected: (List<String>) -> Unit,
    onShowResults: (List<String>) -> Unit
) {
    val selectedItems = remember { mutableStateListOf<String>() }

    LaunchedEffect(selectedOptions) {
        selectedItems.clear()
        selectedItems.addAll(selectedOptions)
    }

    val DanfordFontFamily = FontFamily(
        Font(R.font.danford)
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "Tipo de comida",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = DanfordFontFamily,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(2.dp),
            modifier = Modifier.heightIn(max = 400.dp)
        ) {
            items(options) { option ->
                val label = option["label"].toString()
                val iconResId = option["iconPath"] as Int
                val isSelected = selectedItems.contains(label)

                Column(
                    modifier = Modifier
                        .padding(2.dp)
                        .clickable {
                            if (isSelected) {
                                selectedItems.remove(label)
                            } else {
                                selectedItems.add(label)
                            }
                            onOptionSelected(selectedItems.toList())
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(if (isSelected) Color(0xFFFFA500) else Color.Transparent)
                            .padding(6.dp)
                    ) {
                        Image(
                            painter = painterResource(id = iconResId),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .padding(horizontal = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Visible
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
                onClick = { onShowResults(selectedItems.toList()) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .wrapContentWidth()
                    .height(40.dp)
            ) {
                Text("Mostrar resultados")
            }
        }
    }
}
