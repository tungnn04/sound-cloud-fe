package com.example.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.app.R

enum class ButtonStyle {
    PRIMARY,
    SECONDARY
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle,
    @DrawableRes iconResId: Int? = null
){
    val containerColor: Color
    val contentColor: Color

    when (style) {
        ButtonStyle.PRIMARY -> {
            containerColor = MaterialTheme.colorScheme.primary
            contentColor = MaterialTheme.colorScheme.onPrimary
        }
        ButtonStyle.SECONDARY -> {
            containerColor = MaterialTheme.colorScheme.secondary
            contentColor = MaterialTheme.colorScheme.onSecondary
        }
    }

    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).width(150.dp),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
//        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconResId != null) {
                Icon(
                    painterResource(iconResId),
                    contentDescription = text,
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Preview(showBackground = true, group = "Buttons")
@Composable
fun PreviewSecondaryButtonWithDrawableIcon() {
    AppButton(
        text = "Play",
        onClick = { },
        iconResId = android.R.drawable.ic_media_play, // Ví dụ dùng icon hệ thống
        // iconResId = R.drawable.ic_play_arrow, // Thay R.drawable.ic_play_arrow bằng ID của bạn
        style = ButtonStyle.SECONDARY
    )
}

// Các preview khác giữ nguyên phần text, không cần thay đổi nếu không có icon
@Preview(showBackground = true, group = "Buttons")
@Composable
fun PreviewPrimaryButtonTextOnly() {
    AppButton(
        text = "Create",
        onClick = { },
        style = ButtonStyle.PRIMARY
    )
}

@Preview(showBackground = true, group = "Buttons")
@Composable
fun PreviewSecondaryButtonTextOnly() {
    AppButton(
        text = "Cancel",
        onClick = { },
        style = ButtonStyle.SECONDARY
    )
}

@Preview(showBackground = true, group = "Buttons")
@Composable
fun PreviewDisabledPrimaryButton() {
    AppButton(
        text = "Shuffle",
        onClick = { },
        iconResId = R.drawable.ic_shuffle, // Thay R.drawable.ic_add bằng ID của bạn
        style = ButtonStyle.PRIMARY,
    )
}

@Preview(showBackground = true, group = "Buttons")
@Composable
fun PreviewDisabledSecondaryButton() {
    AppButton(
        text = "Play",
        onClick = { },
        iconResId = R.drawable.ic_play_circle, // Thay R.drawable.ic_cancel bằng ID của bạn
        style = ButtonStyle.SECONDARY,
    )
}

@Preview(showBackground = true, group = "Buttons")
@Composable
fun ButtonRowExample() {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(16.dp)) {
        AppButton(
            text = "Cancel",
            onClick = { },
            style = ButtonStyle.SECONDARY,
            modifier = Modifier.weight(1f)
        )
        AppButton(
            text = "Create",
            onClick = { },
            style = ButtonStyle.PRIMARY,
            modifier = Modifier.weight(1f)
        )
    }
}