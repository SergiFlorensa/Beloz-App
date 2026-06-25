package com.app.beloz.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun EstructuraCuentaItems(
    icon: Int,
    label: String,
    onTap: () -> Unit,
    isEditable: Boolean = false,
    initialValue: String? = null,
    onChanged: ((String) -> Unit)? = null
) {
    var textState by remember { mutableStateOf(TextFieldValue(initialValue ?: "")) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() },
        shape = RoundedCornerShape(22.dp),
        color = BelozColors.Card,
        shadowElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BelozColors.SoftMint
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    tint = BelozColors.MutedGreen,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            if (isEditable) {
                TextField(
                    value = textState,
                    onValueChange = { value ->
                        textState = value
                        onChanged?.invoke(value.text)
                    },
                    placeholder = { Text(label, color = BelozColors.MutedText) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = BelozColors.Ink,
                        unfocusedTextColor = BelozColors.Ink
                    ),
                    textStyle = LocalTextStyle.current.copy(color = BelozColors.Ink),
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = label,
                    color = BelozColors.Ink,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = BelozColors.MutedText
                )
            }
        }
    }
}
