package com.example.app.ui.screens.player

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.ui.components.TopBar


@Composable
fun MusicPlayerScreen(
    musicPlayerViewModel: MusicPlayerViewModel,
    onMinimize: () -> Unit,
) {
    val context = LocalContext.current
    val uiState by musicPlayerViewModel.uiState.collectAsState()
    var isFavorite by remember { mutableStateOf(uiState.currentSong!!.isFavorite) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    Column {
        TopBar(
            title = "",
            onNavigationClick = { onMinimize() },
            onActionClick = {
                musicPlayerViewModel.favoriteChange(uiState.currentSong!!.id, isFavorite)
                isFavorite = !isFavorite
            },
            navigationIcon = R.drawable.ic_chevron_down,
            actionIcon = if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_outline,
            color = Color(0xFF120320),
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF120320))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uiState.currentSong?.coverUrl ?: R.drawable.ic_launcher_background)
                    .crossfade(true)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .build(),
                contentDescription = "Album Art",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = uiState.currentSong?.title ?: "---",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = uiState.currentSong?.artistName ?: "---",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                var sliderDraggingValue by remember { mutableStateOf<Float?>(null) }

                Slider(
                    value = sliderDraggingValue ?: uiState.currentPositionMs.toFloat(),
                    onValueChange = { newValue ->
                        sliderDraggingValue = newValue
                    },
                    onValueChangeFinished = {
                        musicPlayerViewModel.seekToPosition(sliderDraggingValue?.toLong() ?: uiState.currentPositionMs)
                        sliderDraggingValue = null // Reset
                    },
                    modifier = Modifier.fillMaxWidth(),

                    valueRange = 0f..(uiState.durationMs.toFloat().coerceAtLeast(1f)),
                    enabled = uiState.durationMs > 0
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(uiState.currentTimeFormatted, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                    Text(uiState.totalTimeFormatted, style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            PlayerControls(
                modifier = Modifier.fillMaxWidth(),
                isPlaying = uiState.isPlaying,
                onPlayPauseClick = { musicPlayerViewModel.playPause() },
                onNextClick = { musicPlayerViewModel.seekToNext() },
                onPreviousClick = { musicPlayerViewModel.seekToPrevious() }
            )
        }
    }
}

@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    onPlayPauseClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onShuffleClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {}
) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onShuffleClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_shuffle),
                contentDescription = "Shuffle",
                tint = Color.White // TODO: Thay đổi màu khi shuffle bật
            )
        }
        IconButton(onClick = onPreviousClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_skip_previous),
                contentDescription = "Previous",
                tint = Color.White
            )
        }
        IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(64.dp)) {
            Icon(
                painterResource(id = if (isPlaying) R.drawable.ic_pause_circle else R.drawable.ic_play_circle),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
        IconButton(onClick = onNextClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_skip_next),
                contentDescription = "Next",
                tint = Color.White
            )
        }
        IconButton(onClick = onRepeatClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painterResource(id = R.drawable.ic_repeat),
                contentDescription = "Repeat",
                tint = Color.White
            )
        }
    }
}

// --- Preview ---
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun MusicPlayerScreenPreview() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Now Playing") }) },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.fillMaxWidth(0.8f).aspectRatio(1f).background(Color.Gray))
            Spacer(modifier = Modifier.height(24.dp))
            Text("Song Title", style = MaterialTheme.typography.headlineSmall, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Artist Name", style = MaterialTheme.typography.bodyMedium, color = Color.LightGray)
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(value = 0.3f, onValueChange = {}, modifier = Modifier.fillMaxWidth(), valueRange = 0f..1f)
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("1:23", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                    Text("4:05", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            PlayerControls(isPlaying = false)
        }
    }
}