package dev.anilbeesetti.nextplayer.feature.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import dev.anilbeesetti.nextplayer.core.ui.R as coreUiR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

// ── Album art helper ─────────────────────────────────────────────────────────
private fun extractAlbumArt(context: Context, uri: Uri): Bitmap? {
    return runCatching {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bytes = retriever.embeddedPicture
        retriever.release()
        if (bytes != null) BitmapFactory.decodeByteArray(bytes, 0, bytes.size) else null
    }.getOrNull()
}

private fun formatDurationMs(ms: Long): String {
    val secs = ms / 1000
    val h = secs / 3600
    val m = (secs % 3600) / 60
    val s = secs % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

// ── Dedicated Audio Player UI ────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    player: Player,
    title: String,
    artist: String,
    uri: Uri?,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current

    // Album art
    var albumArt by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(uri) {
        albumArt = if (uri != null) {
            withContext(Dispatchers.IO) { extractAlbumArt(context, uri) }
        } else null
    }

    // Playback state
    var isPlaying by remember { mutableStateOf(player.isPlaying) }
    var position by remember { mutableLongStateOf(player.currentPosition) }
    var duration by remember { mutableLongStateOf(player.duration.coerceAtLeast(0)) }
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableLongStateOf(0L) }

    // Rotate album art when playing
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 20_000), RepeatMode.Restart),
        label = "vinylRotation",
    )

    // Poll player state every 200ms
    LaunchedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            override fun onPlaybackStateChanged(state: Int) {
                duration = player.duration.coerceAtLeast(0)
            }
        }
        player.addListener(listener)
        while (isActive) {
            if (!isSeeking) position = player.currentPosition.coerceAtLeast(0)
            duration = player.duration.coerceAtLeast(0)
            delay(200)
        }
        player.removeListener(listener)
    }

    // Background gradient from album art dominant (simple fallback)
    val bgColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                        bgColor,
                    ),
                ),
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_arrow_left),
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                Text(
                    "Now Playing",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Album art (vinyl record style) ────────────────────────────
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (albumArt != null) {
                    Image(
                        bitmap = albumArt!!.asImageBitmap(),
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(if (isPlaying) rotation else rotation),
                    )
                } else {
                    // Default vinyl disc icon
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_music_note),
                        contentDescription = "Music",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(96.dp),
                    )
                }
                // Center hole
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Track info ────────────────────────────────────────────────
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                artist,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Seek bar ──────────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = if (isSeeking) seekPosition.toFloat() else position.toFloat(),
                    onValueChange = { isSeeking = true; seekPosition = it.toLong() },
                    onValueChangeFinished = {
                        player.seekTo(seekPosition)
                        position = seekPosition
                        isSeeking = false
                    },
                    valueRange = 0f..(duration.toFloat().coerceAtLeast(1f)),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        formatDurationMs(if (isSeeking) seekPosition else position),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        formatDurationMs(duration),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Playback controls ─────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Previous
                IconButton(
                    onClick = { if (player.hasPreviousMediaItem()) player.seekToPreviousMediaItem() },
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_skip_prev),
                        contentDescription = "Previous",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp),
                    )
                }

                // Rewind 10s
                IconButton(
                    onClick = { player.seekTo((player.currentPosition - 10_000L).coerceAtLeast(0)) },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_fast),
                        contentDescription = "Rewind 10s",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp).rotate(180f),
                    )
                }

                // Play / Pause — large button
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (player.isPlaying) player.pause() else player.play()
                        },
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            painter = painterResource(
                                if (isPlaying) coreUiR.drawable.ic_pause else coreUiR.drawable.ic_play,
                            ),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                // Fast forward 10s
                IconButton(
                    onClick = { player.seekTo(player.currentPosition + 10_000L) },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_fast),
                        contentDescription = "Forward 10s",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp),
                    )
                }

                // Next
                IconButton(
                    onClick = { if (player.hasNextMediaItem()) player.seekToNextMediaItem() },
                    modifier = Modifier.size(56.dp),
                ) {
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_skip_next),
                        contentDescription = "Next",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }
    }
}
