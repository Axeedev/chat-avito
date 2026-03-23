package com.avito.core.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    text: String,
    content: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3B82F6),
            contentColor = Color.White
        ),
        onClick = {
            onClick()
        }
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = text,
            fontSize = 18.sp
        )
        content()
    }
}
@Composable
fun AppSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        modifier = modifier,
        colors = SwitchDefaults.colors(
            uncheckedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            uncheckedIconColor = MaterialTheme.colorScheme.onPrimary,
            checkedTrackColor = MaterialTheme.colorScheme.tertiary,
            checkedThumbColor = MaterialTheme.colorScheme.onTertiaryFixedVariant
        ),
        checked = checked,
        onCheckedChange = onCheckedChange
    )
}