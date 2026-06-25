package com.app.beloz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.app.beloz.R

@Composable
fun HomeCommandCenter(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF10231E),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .heightIn(min = 218.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F211C),
                            Color(0xFF19382F),
                            Color(0xFF0C1714)
                        )
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 34.dp, y = (-22).dp)
                    .size(122.dp)
                    .clip(CircleShape)
                    .background(Color(0x3345E08A))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-38).dp, y = 30.dp)
                    .size(116.dp)
                    .clip(CircleShape)
                    .background(Color(0x24F2C85B))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF55E49A))
                        )
                        Text(
                            text = "Listo para pedir",
                            color = Color(0xFFCFE8DC),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Beloz",
                        color = Color.White,
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Elige rapido. Pide mejor.",
                        color = Color(0xFFE8F2EC),
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 15.sp,
                        lineHeight = 19.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ActionPill(
                        label = "Beloz AI",
                        iconRes = R.drawable.chat_icon,
                        surfaceColor = Color(0xFF56E39F),
                        contentColor = Color(0xFF10231E),
                        onClick = { navController.navigate("beloz_chat") },
                        modifier = Modifier.widthIn(min = 132.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CompactLink(
                            label = "Locales",
                            iconRes = R.drawable.restaurante,
                            onClick = { navController.navigate("lista_restaurantes") },
                            modifier = Modifier.weight(1f)
                        )
                        CompactLink(
                            label = "Rapido",
                            iconRes = R.drawable.popularidad,
                            onClick = { navController.navigate("relevancia_restaurantes") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .width(112.dp)
                        .height(172.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(width = 98.dp, height = 128.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color(0xFFEAF8EF))
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ramen3d),
                        contentDescription = "Comida destacada",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(width = 118.dp, height = 132.dp)
                            .padding(bottom = 12.dp)
                    )
                    Surface(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFF7D560)
                    ) {
                        Text(
                            text = "15-25 min",
                            color = Color(0xFF10231E),
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionPill(
    label: String,
    iconRes: Int,
    surfaceColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = contentColor,
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CompactLink(
    label: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0x1AFFFFFF)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
