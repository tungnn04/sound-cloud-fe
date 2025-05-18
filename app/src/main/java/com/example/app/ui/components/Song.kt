package com.example.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.app.R
import com.example.app.model.Song

@Composable
fun SongItem(
     song: Song,
     onMoreOptionClick: (Song) -> Unit,
     onPlayClick: (Song) -> Unit,
     currentSong: Song?,
     isPlaying: Boolean
) {
    Row(
         verticalAlignment = Alignment.CenterVertically,
         horizontalArrangement = Arrangement.SpaceBetween,
         modifier = Modifier.fillMaxWidth()
    ) {
          Row {
               AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                         .data(song.coverUrl)
                         .crossfade(true)
                         .build(),
                    contentDescription = "Song cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(80.dp)
                         .clip(RoundedCornerShape(12.dp))
               )
               Column(
                    modifier = Modifier.padding(start = 16.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
               ) {
                    Text(
                         text = song.title,
                         style = MaterialTheme.typography.titleMedium,
                         color = MaterialTheme.colorScheme.onSurface,
                         modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                         text = song.artistName ?: "Unknown artist",
                         style = MaterialTheme.typography.bodyMedium,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
               }
          }

         Row {
              IconButton(onClick = {onPlayClick(song)}) {
                   Icon(
                        painterResource(id = if (currentSong?.id == song.id && isPlaying) R.drawable.ic_pause_circle else R.drawable.ic_play_circle),
                        contentDescription = "Play song",
                        tint = if (currentSong?.id == song.id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(32.dp)
                   )
              }
              IconButton(onClick = { onMoreOptionClick(song) }) {
                   Icon(
                        painterResource(id = R.drawable.ic_dots_vertical),
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface
                   )
              }
         }
    }
}

@Composable
fun ListSong(
     listSong: List<Song>,
     onMoreOptionClick: (Song) -> Unit,
     onPlayClick: (Song) -> Unit,
     currentSong: Song?,
     isPlaying: Boolean
){
     LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
     ) {
          items(listSong.size) { index ->
               SongItem(listSong[index], onPlayClick = onPlayClick, onMoreOptionClick = onMoreOptionClick, currentSong = currentSong, isPlaying = isPlaying )
          }
     }
}
