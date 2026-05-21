package com.myapplication.common.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myapplication.common.ui.theme.TacticalTheme

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun TacticalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isPrimary: Boolean = true
) {
    val backgroundColor = if (isPrimary) TacticalTheme.colors.primary else TacticalTheme.colors.surface
    val contentColor = if (isPrimary) TacticalTheme.colors.onPrimary else TacticalTheme.colors.onSurface

    Surface(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier.height(56.dp).fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        elevation = if (isPrimary) 6.dp else 0.dp,
        border = if (!isPrimary) androidx.compose.foundation.BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.1f)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = contentColor, strokeWidth = 2.dp)
            } else {
                Text(
                    text = text.uppercase(),
                    style = TacticalTheme.typography.button,
                    color = contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
fun TacticalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    Column(modifier = modifier) {
        if (label.isNotEmpty()) {
            Text(
                text = label.uppercase(),
                color = TacticalTheme.colors.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = TacticalTheme.colors.surface.copy(alpha = 0.4f),
            border = androidx.compose.foundation.BorderStroke(1.dp, TacticalTheme.colors.onSurface.copy(alpha = 0.08f))
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = leadingIcon?.let { { Icon(it, null, tint = TacticalTheme.colors.primary, modifier = Modifier.size(20.dp)) } },
                visualTransformation = visualTransformation,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = TacticalTheme.colors.onSurface,
                    cursorColor = TacticalTheme.colors.primary,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                ),
                singleLine = singleLine,
                textStyle = TacticalTheme.typography.subtitle1.copy(color = TacticalTheme.colors.onSurface, fontSize = 14.sp)
            )
        }
    }
}

@OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun TacticalCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = TacticalTheme.colors.surface.copy(alpha = 0.5f),
        elevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(24.dp), content = content)
    }
}
