package com.app.beloz.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun OpcionesFiltro(title: String, options: List<Map<String, Any>>, selectedOption: Map<String, Any>?,
    onOptionSelected: (Map<String, Any>) -> Unit, onShowResults: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge, color = BelozColors.Ink)
        Column(Modifier.selectableGroup()) {
            options.forEach { option ->
                val selected = option == selectedOption
                ListItem(
                    headlineContent = { Text(option["label"].toString()) },
                    trailingContent = { RadioButton(selected, onClick = null) },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).selectable(
                        selected = selected, role = Role.RadioButton, onClick = { onOptionSelected(option) }),
                    colors = ListItemDefaults.colors(containerColor = if (selected) BelozColors.SoftMint else BelozColors.MintSurface)
                )
            }
        }
        Button(onClick = onShowResults, enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Text("Ver restaurantes") }
    }
}
