package com.example.app.ui.screens.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.TopBar
import com.shashank.sony.fancytoastlib.FancyToast
import kotlinx.coroutines.delay

@Composable
fun EditProfileScreen(
    navController: NavController,
    editProfileViewModel: EditProfileViewModel = viewModel(factory = EditProfileViewModel.factory)
) {
    val uiState by editProfileViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val uploadSuccess by editProfileViewModel.uploadSuccess.collectAsState()
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            FancyToast.makeText(
                context,
                "Profile updated successfully",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                false
            ).show()
            navController.navigateUp()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { editProfileViewModel.handleImageUri(context, it) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .zIndex(10f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),

        ) {
            TopBar(
                title = "Edit profile",
                onNavigationClick = { navController.navigateUp() },
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
                if (uiState.avatarImage != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(Uri.fromFile(uiState.avatarImage?.first))
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(uiState.avatarUrl ?: "https://res.cloudinary.com/dcwopmt83/image/upload/v1747213838/user_default_xt53cn.png")
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                AppButton(
                    text = "Choose avatar",
                    onClick = { imagePickerLauncher.launch("image/*") },
                    style = ButtonStyle.PRIMARY
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppTextField(
                    label = "Full name",
                    value = uiState.fullName,
                    onValueChange = { editProfileViewModel.setFullName(it) },
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppButton(
                    text = if (uiState.isLoading) "Updating ..." else "Update",
                    onClick = {
                        editProfileViewModel.updateProfile()
                    },
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}