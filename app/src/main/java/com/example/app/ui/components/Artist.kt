package com.example.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Artist
import com.example.app.ui.MusicScreen


@Composable
fun ArtistItem(
    artist: Artist,
    navController: NavController
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { navController.navigate("${MusicScreen.ARTIST_DETAIL.name}/${artist.id}") }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(artist.profilePicture)
                    .crossfade(true)
                    .build(),
                contentDescription = artist.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(80.dp)
                    .clip(CircleShape)
            )
            Column(
                modifier = Modifier.padding(start = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = artist.name,
                    style = TextStyle(color = Color.White, fontSize = 14.sp),
                    modifier = Modifier.padding(top = 4.dp)
                )

                val numAlbums = artist.albums?.size ?: 0
                val numSongs = artist.songs?.size ?: 0
                val numAlbumsText = if (numAlbums > 1) "$numAlbums albums" else "$numAlbums album"
                val numSongsText = if (numSongs > 1) "$numSongs songs" else "$numSongs song"

                Text(
                    text = "$numAlbumsText | $numSongsText",
                    style = TextStyle(color = Color.Gray, fontSize = 12.sp)
                )
            }
        }
        IconButton(onClick = {}) {
            Icon(
                painterResource(id = R.drawable.ic_dots_vertical),
                contentDescription = "More options",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ListArtist(
    listArtist: List<Artist>,
    navController: NavController
){
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        items(listArtist.size) { index ->
            ArtistItem(artist = listArtist[index], navController = navController)
        }
    }
}