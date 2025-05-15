package com.example.app.ui.screens.auth

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val handleLogin: () -> Unit = {
        coroutineScope.launch {
            try {
                loginViewModel.login()
                navController.navigate(MusicScreen.HOME.name)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("Error", e.message.toString())
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Đăng Nhập",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppTextField(
                label = "Email",
                value = uiState.email,
                onValueChange = { loginViewModel.setEmail(it)},
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                label = "Mật khẩu",
                value = uiState.password,
                onValueChange = { loginViewModel.setPassword(it)},
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Quên mật khẩu?",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { navController.navigate(MusicScreen.FORGOT_PASSWORD.name) }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Đăng nhập",
                onClick = handleLogin,
                style = ButtonStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Chưa có tài khoản?")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Đăng ký ngay",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { navController.navigate(MusicScreen.SIGN_UP.name) }
                )
            }
        }
    }
}
