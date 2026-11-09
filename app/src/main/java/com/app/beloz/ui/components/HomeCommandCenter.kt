package com.app.beloz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeCommandCenter(navController: NavController, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
        Text("BELOZ / REUS", style = MaterialTheme.typography.labelLarge, color = BelozColors.MutedGreen)
        Spacer(Modifier.height(6.dp))
        Text("Hoy, ¿qué te apetece?", style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold, color = BelozColors.Ink)
    }
}
