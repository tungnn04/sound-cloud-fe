package com.example.app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.data.ThemeSetting
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.TopBar

@Composable
fun DarkModeScreen(
    navController: NavController,
    darkModeViewModel: DarkModeViewModel = viewModel(factory = DarkModeViewModel.factory)
) {
    val currentTheme by darkModeViewModel.currentTheme.collectAsState()
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        TopBar(
            title = "Dark theme",
            onNavigationClick = { navController.navigate(MusicScreen.ACCOUNT.name) },
            onActionClick = { },
            navigationIcon = R.drawable.ic_back,
            actionIcon = null,
            color = MaterialTheme.colorScheme.background
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        darkModeViewModel.onThemeSettingChange(ThemeSetting.DARK)
                    }
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Dark",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                if (currentTheme == ThemeSetting.DARK) {
                    Icon(
                        painterResource(id = R.drawable.ic_check),
                        contentDescription = "Dark",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        darkModeViewModel.onThemeSettingChange(ThemeSetting.LIGHT)
                    }
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Light",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                if (currentTheme == ThemeSetting.LIGHT) {
                    Icon(
                        painterResource(id = R.drawable.ic_check),
                        contentDescription = "",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        darkModeViewModel.onThemeSettingChange(ThemeSetting.SYSTEM)
                    }
                    .padding(horizontal = 8.dp ,vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "System",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.weight(1f))
                if (currentTheme == ThemeSetting.SYSTEM) {
                    Icon(
                        painterResource(id = R.drawable.ic_check),
                        contentDescription = "Dark",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}