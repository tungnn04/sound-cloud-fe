package com.example.app.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.app.R
import com.example.app.ui.MusicScreen
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    signUpViewModel: SignUpViewModel = viewModel(factory = SignUpViewModel.Factory),
    navController: NavController
) {
    val uiState by signUpViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val handleRegister: () -> Unit = {
        coroutineScope.launch {
            try {
                signUpViewModel.register()
                FancyToast.makeText(context, "Sign up successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show()
                delay(1000)
                navController.navigate(MusicScreen.LOGIN.name)
            } catch (e: Exception) {
                delay(300)
                FancyToast.makeText(context, e.message, FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                Log.e("Error", e.message.toString())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal =  16.dp)
            .verticalScroll(scrollState)
            .imePadding(),
//        verticalArrangement = Arrangement.Center,
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
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Start,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            label = "Email",
            value = uiState.email,
            onValueChange = { signUpViewModel.setEmail(it)},
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            label = "Full name",
            value = uiState.fullName,
            onValueChange = { signUpViewModel.setFullName(it) },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        AppTextField(
            label = "Password",
            value = uiState.password,
            onValueChange = { signUpViewModel.setPassword(it) },
            isPassword = true,
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        AppTextField(
            label = "Confirm Password",
            value = uiState.confirmPassword,
            onValueChange = { signUpViewModel.setConfirmPassword(it) },
            isPassword = true,
            modifier = Modifier,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = "Sign Up",
            onClick = {
                focusManager.clearFocus()
                handleRegister()
            },
            modifier = Modifier.fillMaxWidth(),
            style = ButtonStyle.PRIMARY
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(
                text = "Already have an account ?",
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Login",
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { navController.popBackStack() }
            )
        }

    }
}
