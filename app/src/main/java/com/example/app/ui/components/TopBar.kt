package com.example.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun TopBar (
    title: String,
    onNavigationClick: () -> Unit,
    onActionClick: () -> Unit,
    @DrawableRes navigationIcon: Int,
    @DrawableRes actionIcon: Int?,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().background(color).padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigationClick) {
            Icon(
                painterResource(id = navigationIcon),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (actionIcon != null) {
            IconButton(onClick = onActionClick) {
                Icon(
                    painterResource(id = actionIcon),
                    contentDescription = "Action",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        else {
            IconButton(onClick = {}) {
                Icon(
                    painterResource(id = navigationIcon),
                    contentDescription = "null",
                    tint = color
                )
            }
        }
    }
}
