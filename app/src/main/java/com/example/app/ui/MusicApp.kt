package com.example.app.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Song
import com.example.app.ui.screens.account.AccountScreen
import com.example.app.ui.screens.account.EditProfileScreen
import com.example.app.ui.screens.account.UploadSongScreen
import com.example.app.ui.screens.auth.ForgotPasswordScreen
import com.example.app.ui.screens.home.HomeScreen
import com.example.app.ui.screens.auth.LoginScreen
import com.example.app.ui.screens.auth.LoginViewModel
import com.example.app.ui.screens.auth.SignUpScreen
import com.example.app.ui.screens.favourite.FavoriteScreen
import com.example.app.ui.screens.home.AlbumDetailScreen
import com.example.app.ui.screens.home.ArtistDetailScreen
import com.example.app.ui.screens.home.SearchScreen
import com.example.app.ui.screens.player.MusicPlayerScreen
import com.example.app.ui.screens.player.MusicPlayerUiState
import com.example.app.ui.screens.player.MusicPlayerViewModel
import com.example.app.ui.screens.playlist.PlaylistDetailScreen
import com.example.app.ui.screens.playlist.PlaylistScreen
import kotlinx.coroutines.launch

enum class MusicScreen {
    HOME,
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD,
    LOADING,
    ACCOUNT,
    PLAYLIST,
    FAVORITE,
    SEARCH,
    ALBUM_DETAIL,
    ARTIST_DETAIL,
    PLAYLIST_DETAIL,
    UPLOAD_SONG,
    EDIT_PROFILE,
}

@Composable
fun MusicApp() {
    val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory)
    val musicPlayerViewModel: MusicPlayerViewModel = viewModel(factory = MusicPlayerViewModel.factory)
    val uiState by musicPlayerViewModel.uiState.collectAsState()
    var isPlayerScreenVisible by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val coroutineScope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime

    val imeVisible by remember {
        derivedStateOf {
            imeInsets.getBottom(density) > 0
        }
    }

    val handlePlayClick: (Int) -> Unit = { songId ->
        coroutineScope.launch {
            musicPlayerViewModel.playSongImmediately(songId)
        }
    }

    val handlePlayNextClick: (Int) -> Unit = { songId ->
        coroutineScope.launch {
            musicPlayerViewModel.playSongNext(songId)
        }
    }

    val handlePlayAll: (List<Song>, Boolean) -> Unit = { songs, isShuffle ->
        coroutineScope.launch {
            musicPlayerViewModel.loadPlaylist(songs = if (!isShuffle) songs else songs.shuffled(), startShuffle = isShuffle)
        }
    }

    Scaffold(
        bottomBar = {
            if (!imeVisible && !isPlayerScreenVisible && currentRoute != MusicScreen.LOGIN.name
                && currentRoute != MusicScreen.SIGN_UP.name && currentRoute != MusicScreen.FORGOT_PASSWORD.name
                && currentRoute != MusicScreen.LOADING.name) {
                Column {
                    if (uiState.currentSong != null) {
                        MiniPlayerBar(
                            uiState = uiState,
                            onPlayPauseClick = {
                                musicPlayerViewModel.playPause()
                                Log.e("PlayPause", uiState.isPlaying.toString())
                                               },
                            onNextClick = { musicPlayerViewModel.seekToNext() },
                            onBarClick = {
                                isPlayerScreenVisible = true
                            }
                        )
                    }
                    BottomNavigation(navController, selectedItem, onChange = { selectedItem = it})
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize().padding(innerPadding),
            navController = navController,
            startDestination = MusicScreen.LOADING.name
        ) {
            composable(MusicScreen.LOADING.name) {
                LaunchedEffect(Unit) {
                    try {
                        loginViewModel.auth()
                        navController.navigate(MusicScreen.HOME.name) {
                            popUpTo(MusicScreen.LOADING.name) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        Log.e("Error", e.message.toString())
                        navController.navigate(MusicScreen.LOGIN.name) {
                            popUpTo(MusicScreen.LOADING.name) { inclusive = true }
                        }
                    }
                }
                Text("Loading")
            }
            composable(MusicScreen.HOME.name) {
                HomeScreen(navController = navController, onPlayClick = handlePlayClick)
            }
            composable(MusicScreen.LOGIN.name) {
                LoginScreen(navController = navController)
            }
            composable(MusicScreen.SIGN_UP.name) {
                SignUpScreen(navController = navController)
            }
            composable(MusicScreen.FORGOT_PASSWORD.name) {
                ForgotPasswordScreen(navController = navController)
            }
            composable(MusicScreen.ACCOUNT.name) {
                AccountScreen(navController = navController, onPlayClick = handlePlayClick,
                    onPlayNextClick = handlePlayNextClick,
                    currentSong = uiState.currentSong, isPlaying = uiState.isPlaying)
            }
            composable(MusicScreen.PLAYLIST.name) {
                PlaylistScreen(navController = navController)
            }
            composable(MusicScreen.FAVORITE.name) {
                FavoriteScreen(navController = navController, onPlayClick = handlePlayClick,
                    onPlayNextClick = handlePlayNextClick,
                    onPlayAll = handlePlayAll, currentSong = uiState.currentSong, isPlaying = uiState.isPlaying)
            }
            composable(MusicScreen.SEARCH.name) {
                SearchScreen(navController = navController, onPlayClick = handlePlayClick,
                    onPlayNextClick = handlePlayNextClick,
                    currentSong = uiState.currentSong, isPlaying = uiState.isPlaying)
            }
            composable(
                "${MusicScreen.ALBUM_DETAIL.name}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val albumId = it.arguments?.getInt("id") ?: -1
                 AlbumDetailScreen(
                     navController = navController,
                     albumId = albumId,
                     onPlayClick = handlePlayClick,
                     onPlayAll = handlePlayAll,
                     currentSong = uiState.currentSong,
                     isPlaying = uiState.isPlaying,
                     onPlayNextClick = handlePlayNextClick,
                 )
            }
            composable(
                "${MusicScreen.ARTIST_DETAIL.name}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val artistId = it.arguments?.getInt("id") ?: -1
                ArtistDetailScreen(
                    navController = navController,
                    artistId = artistId,
                    onPlayClick = handlePlayClick,
                    onPlayNextClick = handlePlayNextClick,
                    onPlayAll = handlePlayAll,
                    currentSong = uiState.currentSong,
                    isPlaying = uiState.isPlaying
                )
            }
            composable(
                "${MusicScreen.PLAYLIST_DETAIL.name}/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) {
                val playlistId = it.arguments?.getInt("id") ?: -1
                PlaylistDetailScreen(
                    navController = navController,
                    playlistId = playlistId,
                    onPlayClick = handlePlayClick,
                    onPlayNextClick = handlePlayNextClick,
                    onPlayAll = handlePlayAll,
                    currentSong = uiState.currentSong,
                    isPlaying = uiState.isPlaying
                )
            }
            composable(MusicScreen.UPLOAD_SONG.name) {
                UploadSongScreen(navController = navController)
            }
            composable(MusicScreen.EDIT_PROFILE.name) {
                EditProfileScreen(navController = navController)
            }
        }
        if (isPlayerScreenVisible) {
            MusicPlayerScreen(
                musicPlayerViewModel = musicPlayerViewModel,
                onMinimize = { isPlayerScreenVisible = false },
                onPlayClick = handlePlayClick,
                onPlayNextClick = handlePlayNextClick,
            )
        }
    }
}

@Composable
fun BottomNavigation(
    navController: NavController,
    selectedItem: Int,
    onChange: (Int) -> Unit = {}
) {
    val items = listOf(
        MusicScreen.HOME,
        MusicScreen.PLAYLIST,
        MusicScreen.FAVORITE,
        MusicScreen.ACCOUNT
    )
    val icons = listOf(
        R.drawable.ic_home,
        R.drawable.ic_playlist,
        R.drawable.ic_favorite_outline,
        R.drawable.ic_account
    )

    NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
            .height(100.dp)
    ) {
        items.forEachIndexed { index, screen ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = icons[index]), contentDescription = null) },
                label = { Text(screen.name) },
                selected = selectedItem == index,
                onClick = {
                    onChange(index)
                    navController.navigate(screen.name) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Composable
fun MiniPlayerBar(
    modifier: Modifier = Modifier,
    uiState: MusicPlayerUiState,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onBarClick: () -> Unit
) {
    val song = uiState.currentSong ?: return

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
               AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(song.coverUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Song cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.padding(start = 8.dp)
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onBarClick() }
               )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f).clickable { onBarClick() }) {
                    Text(
                        text = song.title,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = song.artistName ?: "Unknown Artist",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onPlayPauseClick) {
                    Icon(
                        painterResource(id = if (uiState.isPlaying) R.drawable.ic_pause_circle else R.drawable.ic_play_circle),
                        contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = onNextClick) {
                    Icon(
                        painterResource(id = R.drawable.ic_skip_next),
                        contentDescription = "Next",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        LinearProgressIndicator(
            progress = { uiState.progress },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}