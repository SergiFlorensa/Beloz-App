package com.app.beloz.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = Color(0xFF10231E),
        shadowElevation = 6.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tipo de comida",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Elige uno o varios estilos",
                        color = Color(0xFFCFE8DC),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp
                    )
                }

                if (selectedItems.isNotEmpty()) {
                    Text(
                        text = "${selectedItems.size} activos",
                        color = Color(0xFFF7D560),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 420.dp)
            ) {
                items(options) { option ->
                    val label = option["label"].toString()
                    val iconResId = option["iconPath"] as Int
                    val isSelected = selectedItems.contains(label)

                    FoodTypeOption(
                        label = label,
                        iconResId = iconResId,
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                selectedItems.remove(label)
                            } else {
                                selectedItems.add(label)
                            }
                            onOptionSelected(selectedItems.toList())
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { onShowResults(selectedItems.toList()) },
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
private fun FoodTypeOption(
    label: String,
    iconResId: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val popScale by animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 520f),
        label = "foodTypeSelectionPop"
    )

    Surface(
        modifier = Modifier
            .height(112.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) Color(0x20FFFFFF) else Color(0x14FFFFFF),
        border = BorderStroke(1.dp, if (selected) Color(0x59FFB24D) else Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .graphicsLayer(scaleX = popScale, scaleY = popScale),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    PaintBlob(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                rotationZ = ((label.length % 7) - 3) * 4f,
                                scaleX = 1.05f,
                                scaleY = 0.96f
                            )
                    )
                }
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = label,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PaintBlob(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w * 0.12f, h * 0.52f)
            cubicTo(w * 0.02f, h * 0.30f, w * 0.26f, h * 0.18f, w * 0.31f, h * 0.08f)
            cubicTo(w * 0.42f, h * -0.08f, w * 0.55f, h * 0.18f, w * 0.70f, h * 0.12f)
            cubicTo(w * 0.94f, h * 0.02f, w * 0.90f, h * 0.36f, w * 0.98f, h * 0.48f)
            cubicTo(w * 1.10f, h * 0.68f, w * 0.76f, h * 0.66f, w * 0.74f, h * 0.84f)
            cubicTo(w * 0.71f, h * 1.06f, w * 0.45f, h * 0.82f, w * 0.31f, h * 0.96f)
            cubicTo(w * 0.14f, h * 1.10f, w * 0.23f, h * 0.70f, w * 0.12f, h * 0.52f)
            close()
        }
        val strokeTail = Path().apply {
            moveTo(w * 0.08f, h * 0.74f)
            cubicTo(w * 0.00f, h * 0.82f, w * 0.10f, h * 0.95f, w * 0.23f, h * 0.88f)
            cubicTo(w * 0.18f, h * 0.80f, w * 0.16f, h * 0.75f, w * 0.08f, h * 0.74f)
            close()
        }
        val topFlick = Path().apply {
            moveTo(w * 0.70f, h * 0.07f)
            cubicTo(w * 0.76f, h * -0.03f, w * 0.89f, h * 0.05f, w * 0.82f, h * 0.17f)
            cubicTo(w * 0.76f, h * 0.15f, w * 0.73f, h * 0.12f, w * 0.70f, h * 0.07f)
            close()
        }
        drawPath(path = path, color = Color(0xFFFFB24D))
        drawPath(path = strokeTail, color = Color(0xFFFFB24D))
        drawPath(path = topFlick, color = Color(0xFFFFB24D))
    }
}
