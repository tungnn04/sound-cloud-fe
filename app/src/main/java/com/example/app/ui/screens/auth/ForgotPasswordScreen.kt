package com.example.app.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.TopBar
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.delay
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
                FancyToast.makeText(context, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
            }
        }
    }

    fun handleResendEmail() {
        coroutineScope.launch {
            try {
                forgotPwViewModel.forgotPassword()
                FancyToast.makeText(context, "Resend email successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
            } catch (e: Exception) {
                FancyToast.makeText(context, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
            }
        }
    }

    fun handleVerifyOtp() {
        coroutineScope.launch {
            try {
                forgotPwViewModel.verifyOTP()
                currentStep = 3
            } catch (e: Exception) {
                FancyToast.makeText(context, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
            }
        }
    }

    fun handleResetPassword() {
        coroutineScope.launch {
            try {
                forgotPwViewModel.resetPassword()
                FancyToast.makeText(context, "Reset password successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
                delay(1000)
                navController.navigate(MusicScreen.LOGIN.name)
            } catch (e: Exception) {
                FancyToast.makeText(context, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
            }
        }
    }
    Column(
    ) {
        when (currentStep) {
            1 -> {
                TopBar(
                    title = "Forgot Password",
                    onNavigationClick = { navController.popBackStack() },
                    navigationIcon = R.drawable.ic_back,
                    actionIcon = null,
                    onActionClick = {},
                    color = MaterialTheme.colorScheme.background,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Please enter your email address to reset password.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(Modifier.height(32.dp))
                    AppTextField(
                        label = "Email",
                        value = uiState.email,
                        onValueChange = { forgotPwViewModel.setEmail(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AppButton(
                        text = if (uiState.isLoading) "Sending email ..." else "Send email",
                        onClick = { handleForgotPassword() },
                        style = ButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
            2 -> {
                TopBar(
                    title = "Verify Code",
                    onNavigationClick = { currentStep = 1 },
                    navigationIcon = R.drawable.ic_back,
                    actionIcon = null,
                    onActionClick = {},
                    color = MaterialTheme.colorScheme.background,
                )
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "We sent a reset link to ${uiState.email} enter 6 digit code that mentioned in the email",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    OtpInputField(
                        otpText = uiState.otp,
                        onOtpTextChange = { forgotPwViewModel.setOtp(it) }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AppButton(
                        text = "Verify Code",
                        onClick = { handleVerifyOtp() },
                        style = ButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Haven't received the code ?",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Resend Email",
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { handleResendEmail() }
                        )
                    }
                }
            }
            3 -> {
                TopBar(
                    title = "Reset Password",
                    onNavigationClick = { currentStep = 2 },
                    navigationIcon = R.drawable.ic_back,
                    actionIcon = null,
                    onActionClick = {},
                    color = MaterialTheme.colorScheme.background,
                )
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Create a new password. Ensure it differs from previous ones for security",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AppTextField(
                        label = "Password",
                        value = uiState.password,
                        isPassword = true,
                        onValueChange = { forgotPwViewModel.setPassword(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    AppTextField(
                        label = "Confirm Password",
                        value = uiState.confirmPassword,
                        isPassword = true,
                        onValueChange = { forgotPwViewModel.setConfirmPassword(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    AppButton(
                        text = "Update Password",
                        onClick = { handleResetPassword() },
                        style = ButtonStyle.PRIMARY,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun OtpInputField(
    otpText: String,
    onOtpTextChange: (String) -> Unit,
    otpCount: Int = 6
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = otpText,
                selection = TextRange(otpText.length)
            )
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = textFieldValueState,
        onValueChange = {
            if (it.text.length <= otpCount) {
                textFieldValueState = it
                onOtpTextChange(it.text)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(otpCount) { index ->
                    OtpCell(
                        index = index,
                        text = otpText
                    )
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
    )
}

@Composable
fun OtpCell(
    index: Int,
    text: String
) {
    val isFocused = text.length == index
    val char = when {
        index >= text.length -> ""
        else -> text[index].toString()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isFocused) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = char,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
    }
}