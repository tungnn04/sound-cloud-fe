package com.example.app.ui.screens.account

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.app.model.Album
import com.example.app.model.Artist
import com.example.app.model.Category
import com.example.app.ui.components.AppButton
import com.example.app.ui.components.AppTextField
import com.example.app.ui.components.ButtonStyle
import com.example.app.ui.components.TopBar
import com.shashank.sony.fancytoastlib.FancyToast

@Composable
fun UploadSongScreen(
    navController: NavController,
    uploadSongViewModel: UploadSongViewModel = viewModel(factory = UploadSongViewModel.factory),
) {
    val uiState by uploadSongViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val errorMessage by uploadSongViewModel.errorMessage.collectAsState()
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            FancyToast.makeText(context, it , FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show()
            uploadSongViewModel.clearError()
        }
    }

    val uploadSuccess by uploadSongViewModel.uploadSuccess.collectAsState()
    LaunchedEffect(uploadSuccess) {
        if (uploadSuccess) {
            FancyToast.makeText(context, "Upload song successfully", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
            uploadSongViewModel.resetUploadSuccess()
        }
    }

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadSongViewModel.handleAudioUri(context, it) }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { uploadSongViewModel.handleImageUri(context, it) }
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

        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                title = "Upload Song",
                onNavigationClick = { navController.navigateUp() },
                onActionClick = { },
                navigationIcon = R.drawable.ic_back,
                actionIcon = null,
                color = MaterialTheme.colorScheme.background
            )
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppTextField(
                    label = "Song title",
                    value = uiState.title,
                    onValueChange = { uploadSongViewModel.updateTitle(it) },
                    modifier = Modifier
                )
                Spacer(modifier = Modifier.height(8.dp))
                ArtistDropdown(
                    artists = uiState.artists,
                    selectedArtistId = uiState.artistId,
                    onArtistSelected = { uploadSongViewModel.updateArtistId(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AlbumDropdown(
                    albums = uiState.albums,
                    selectedAlbumId = uiState.albumId,
                    onAlbumSelected = { uploadSongViewModel.updateAlbumId(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CategoryDropdown(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.categoryId,
                    onCategorySelected = { uploadSongViewModel.updateCategoryId(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                AudioFilePicker(
                    audioUri = uiState.audioFile?.first?.let { Uri.fromFile(it) },
                    onPickAudio = {
                        audioPickerLauncher.launch("audio/*")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                CoverImagePicker(
                    coverImageUri = uiState.coverImage?.first?.let { Uri.fromFile(it) },
                    onPickImage = {
                        imagePickerLauncher.launch("image/*")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Spacer(modifier = Modifier.height(16.dp))

                AppButton(
                    text = if (uiState.isLoading) "Uploading ..." else "Upload",
                    onClick = { uploadSongViewModel.uploadSong() },
                    style = ButtonStyle.PRIMARY,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDropdown(
    artists: List<Artist>,
    selectedArtistId: Int?,
    onArtistSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedArtist = artists.find { it.id == selectedArtistId }

    Text(
        text = "Artist",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedArtist?.name ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    "Choose artist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onArtistSelected(null)
                    expanded = false
                }
            )

            artists.forEach { artist ->
                DropdownMenuItem(
                    text = { Text(artist.name) },
                    onClick = {
                        onArtistSelected(artist.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumDropdown(
    albums: List<Album>,
    selectedAlbumId: Int?,
    onAlbumSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAlbum = albums.find { it.id == selectedAlbumId }

    Text(
        text = "Album",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedAlbum?.title ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    "Choose album",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
//                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
//                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onAlbumSelected(null)
                    expanded = false
                }
            )

            albums.forEach { album ->
                DropdownMenuItem(
                    text = { Text(album.title) },
                    onClick = {
                        onAlbumSelected(album.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategoryId: Int?,
    onCategorySelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedCategory = categories.find { it.id == selectedCategoryId }

    Text(
        text = "Category",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {

        OutlinedTextField(
            value = selectedCategory?.name ?: "",
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    "Choose category",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .exposedDropdownSize(false)
                .background(MaterialTheme.colorScheme.secondaryContainer)

        ) {
            DropdownMenuItem(
                text = {
                    Text("None")
                },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )

            categories.forEach { category ->
                DropdownMenuItem(
                    text = {Text(category.name) },
                    onClick = {
                        onCategorySelected(category.id)
                        expanded = false
                    }
                )
            }
        }

    }
}

@Composable
fun AudioFilePicker(
    audioUri: Uri?,
    onPickAudio: () -> Unit
) {
    Column {
        Text(
            text = "Audio file",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onPickAudio)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = audioUri?.path?.substringAfterLast("/") ?: "Choose audio file",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun CoverImagePicker(
    coverImageUri: Uri?,
    onPickImage: () -> Unit
) {
    Column {
        Text(
            text = "Cover image",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(onClick = onPickImage),
            contentAlignment = Alignment.Center
        ) {
            if (coverImageUri != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(coverImageUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Ảnh bìa",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Choose cover image",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

