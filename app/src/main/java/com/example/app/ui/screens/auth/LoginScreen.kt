package com.example.app.ui.screens.auth

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    startService: () -> Unit,
    loadRecentlySong: () -> Unit,
    updateSelectedItem: () -> Unit,
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val handleLogin: () -> Unit = {
        coroutineScope.launch {
            try {
                loginViewModel.login()
                navController.navigate(MusicScreen.HOME.name)
                updateSelectedItem()
                startService()
                loadRecentlySong()
            } catch (e: Exception) {
                delay(300)
                FancyToast.makeText(context, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false ).show()
                Log.e("Error", e.message.toString())
            }
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
//            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter =  painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(200.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                isPassword = true,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Forgot password ?",
                color = MaterialTheme.colorScheme.onBackground,
                fontStyle = FontStyle.Italic,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { navController.navigate(MusicScreen.FORGOT_PASSWORD.name) }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AppButton(
                text = "Login",
                onClick = handleLogin,
                style = ButtonStyle.PRIMARY,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(
                    text = "Don't have an account ?",
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { navController.navigate(MusicScreen.SIGN_UP.name) }
                )
            }
        }
    }
}
