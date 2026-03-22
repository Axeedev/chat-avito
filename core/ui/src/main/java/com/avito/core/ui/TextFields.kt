package com.avito.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    placeholderText: String,
    leadingIconId: Int,
    value: String,
    onValueChange: (String) -> Unit = {}
) {
    TextField(
        modifier = modifier
            .border(
                BorderStroke(1.dp, Color.Gray.copy(0.05f)),
                shape = RoundedCornerShape(25.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        onValueChange = onValueChange,
        value = value,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Gray.copy(alpha = 0.1f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        textStyle = TextStyle(
            color = Color.Black
        ),
        placeholder = {
            Text(
                text = placeholderText,
                color = Color.Gray.copy()
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIconId),
                contentDescription = "set time",
            )
        }
    )
}



@Composable
fun AuthTextField(
    modifier: Modifier = Modifier,
    value: String,
    isError: Boolean,
    enabled: Boolean = true,
    placeholderText: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
        value = value,
        singleLine = true,
        enabled = enabled,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholderText
            )
        },
        isError = isError,
        textStyle = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
        ),
        colors = TextFieldDefaults.colors(
            cursorColor = Color.Gray,
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorTextColor = Color.Red,
            errorIndicatorColor = Color.Transparent
        ),
        visualTransformation = visualTransformation
    )
}