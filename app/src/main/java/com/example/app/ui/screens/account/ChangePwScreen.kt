package com.example.app.ui.screens.account

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.TopBar
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    changePwViewModel: ChangePwViewModel = viewModel(factory = ChangePwViewModel.factory)
) {
    val uiState by changePwViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val handleChangePw: () -> Unit = {
        coroutineScope.launch {
            try {
                changePwViewModel.changePassword()
                FancyToast.makeText(context, "Password changed successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
                navController.navigateUp()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    Column(
    ) {
        TopBar(
            title = "Change Password",
            onNavigationClick = {navController.navigateUp()},
            onActionClick = {},
            navigationIcon = R.drawable.ic_back,
            actionIcon = null,
            color = MaterialTheme.colorScheme.background
        )
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppTextField(
                value = uiState.oldPassword,
                onValueChange = { changePwViewModel.setOldPassword(it) },
                label = "Current Password",
                isPassword = true,
                modifier = Modifier
            )

            AppTextField(
                value = uiState.newPassword,
                onValueChange = { changePwViewModel.setNewPassword(it) },
                label = "New Password",
                isPassword = true,
                modifier = Modifier
            )

            AppTextField(
                value = uiState.confirmPassword,
                onValueChange = { changePwViewModel.setConfirmPassword(it) },
                label = "Confirm Password",
                isPassword = true,
                modifier = Modifier
            )
            Spacer(Modifier.height(16.dp))
            AppButton(
                text = "Change Password",
                onClick = {
                    handleChangePw()
                },
                modifier = Modifier.fillMaxWidth(),
                style = ButtonStyle.PRIMARY
            )
        }
    }
}