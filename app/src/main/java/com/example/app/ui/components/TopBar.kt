package com.example.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar (
    title: String,
    onNavigationClick: () -> Unit,
    onActionClick: () -> Unit,
    @DrawableRes navigationIcon: Int,
    @DrawableRes actionIcon: Int,
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
                tint = Color.White
            )
        }
        Text(
            text = title,
            style = TextStyle(color = Color.White, fontSize = 20.sp),
        )
        IconButton(onClick = onActionClick) {
            Icon(
                painterResource(id = actionIcon),
                contentDescription = "Action",
                tint = Color.White
            )
        }
    }
}
