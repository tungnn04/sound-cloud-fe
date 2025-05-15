package com.example.app.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    forgotPwViewModel: ForgotPasswordViewModel = viewModel(factory = ForgotPasswordViewModel.Factory)
) {
    val uiState by forgotPwViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }

    fun handleForgotPassword() {
        coroutineScope.launch {
            try {
                forgotPwViewModel.forgotPassword()
                currentStep = 2
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleVerifyOtp() {
        coroutineScope.launch {
            try {
                forgotPwViewModel.verifyOTP()
                currentStep = 3
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun handleResetPassword() {
        coroutineScope.launch {
            try {
                forgotPwViewModel.resetPassword()
                Toast.makeText(context, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show()
                navController.navigate(MusicScreen.LOGIN.name)
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentStep) {
            1 -> {
                AppTextField(
                    label = "Email",
                    value = uiState.email,
                    onValueChange = { forgotPwViewModel.setEmail(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                AppButton(
                    text = "Tiếp tục",
                    onClick = { handleForgotPassword() },
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Quay lại")
                }
            }
            2 -> {
                AppTextField(
                    label = "Mã OTP",
                    value = uiState.otp,
                    onValueChange = { forgotPwViewModel.setOtp(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                AppButton(
                    text = "Xác nhận",
                    onClick = { handleVerifyOtp() },
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
                Button(onClick = { currentStep = 1 }) {
                    Text("Quay lại")
                }
            }
            3 -> {
                AppTextField(
                    label = "Mật khẩu mới",
                    value = uiState.password,
                    onValueChange = { forgotPwViewModel.setPassword(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                AppTextField(
                    label = "Nhập lại mật khẩu",
                    value = uiState.confirmPassword,
                    onValueChange = { forgotPwViewModel.setConfirmPassword(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                AppButton(
                    text = "Xác nhận",
                    onClick = { handleResetPassword() },
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

    }
}
