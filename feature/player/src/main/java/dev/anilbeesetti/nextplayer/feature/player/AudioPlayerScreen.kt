package dev.anilbeesetti.nextplayer.feature.player

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.audiofx.Visualizer
import android.net.Uri
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import dev.anilbeesetti.nextplayer.core.ui.R as coreUiR
import dev.anilbeesetti.nextplayer.core.ui.designsystem.NextIcons
import dev.anilbeesetti.nextplayer.feature.player.state.AudioEqualizerState
import dev.anilbeesetti.nextplayer.feature.player.state.rememberAudioEqualizerState
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

// ── Audio Visualizer ─────────────────────────────────────────────────────────
@Composable
fun AudioVisualizerView(
    player: Player,
    modifier: Modifier = Modifier,
) {
    var visualizer by remember { mutableStateOf<Visualizer?>(null) }
    var waveData by remember { mutableStateOf(ByteArray(0)) }
    val barColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)

    LaunchedEffect(player) {
        // Wait until audio session id is available
        var sessionId = 0
        while (isActive) {
            runCatching {
                val id = (player as? androidx.media3.session.MediaController)?.audioSessionId ?: 0
                if (id != 0) { sessionId = id; return@runCatching }
            }
            delay(200)
        }
        if (sessionId != 0) {
            try {
                val viz = Visualizer(sessionId)
                viz.captureSize = Visualizer.getCaptureSizeRange()[0]
                viz.setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(v: Visualizer?, waveform: ByteArray?, samplingRate: Int) {
                        waveform?.let { waveData = it }
                    }
                    override fun onFftDataCapture(v: Visualizer?, fft: ByteArray?, samplingRate: Int) {}
                }, Visualizer.getMaxCaptureRate() / 2, true, false)
                viz.enabled = true
                visualizer = viz
            } catch (_: Exception) { }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching { visualizer?.release() }
        }
    }

    val data = waveData
    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas
        val barWidth = size.width / data.size
        val centerY = size.height / 2f
        data.forEachIndexed { i, byte ->
            val amplitude = (byte.toFloat() / 128f) * centerY
            drawLine(
                color = barColor,
                start = Offset(i * barWidth, centerY - amplitude),
                end = Offset(i * barWidth, centerY + amplitude),
                strokeWidth = barWidth.coerceAtLeast(1f),
            )
        }
    }
}

// ── Audio Settings Dialog ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioSettingsDialog(
    player: Player,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val audioEqualizerState = rememberAudioEqualizerState(player)

    // Playback Speed
    var playbackSpeed by remember { mutableFloatStateOf(player.playbackParameters.speed) }
    val speeds = listOf(0.5f, 0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f, 2.5f, 3f)

    // Skip Silence
    var skipSilence by remember { mutableStateOf(false) }
    LaunchedEffect(skipSilence) {
        runCatching {
            if (player is androidx.media3.exoplayer.ExoPlayer) {
                player.skipSilenceEnabled = skipSilence
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Audio Settings") },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Playback Speed
                item {
                    Text("Playback Speed", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            String.format("%.2fx", playbackSpeed),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Slider(
                        value = playbackSpeed,
                        onValueChange = { newSpeed ->
                            playbackSpeed = newSpeed
                            runCatching {
                                player.playbackParameters = androidx.media3.common.PlaybackParameters(newSpeed)
                            }
                        },
                        valueRange = 0.5f..3f,
                        steps = 10,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        speeds.forEach { speed ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        playbackSpeed = speed
                                        runCatching {
                                            player.playbackParameters = androidx.media3.common.PlaybackParameters(speed)
                                        }
                                    },
                                shape = RoundedCornerShape(4.dp),
                                color = if (playbackSpeed == speed) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Text(
                                    String.format("%.1fx", speed),
                                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center,
                                    color = if (playbackSpeed == speed) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }

                // Skip Silence
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Skip Silence", style = MaterialTheme.typography.bodyLarge)
                            Text("Automatically skip silent parts", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = skipSilence, onCheckedChange = { skipSilence = it })
                    }
                }

                // Audio Equalizer
                item {
                    if (audioEqualizerState.isReady && audioEqualizerState.bandCount > 0) {
                        Text("Audio Equalizer", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        audioEqualizerState.bandLevels.forEachIndexed { index, level ->
                            val freq = audioEqualizerState.bandFrequencies.getOrElse(index) { "" }
                            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(text = freq, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "${if (level >= 0) "+" else ""}${level / 100} dB",
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                                Slider(
                                    value = level.toFloat(),
                                    onValueChange = { audioEqualizerState.setBandLevel(index, it.toInt()) },
                                    valueRange = audioEqualizerState.minLevel.toFloat()..audioEqualizerState.maxLevel.toFloat(),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = { audioEqualizerState.resetBands() }) { Text("Reset EQ") }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        },
    )
}

// ── Queue Bottom Sheet ─────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueBottomSheet(
    player: Player,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentIndex = player.currentMediaItemIndex
    val itemCount = player.mediaItemCount

    val items = remember(itemCount, currentIndex) {
        buildList {
            for (i in 0 until itemCount) {
                val item = player.getMediaItemAt(i)
                add(
                    QueueItem(
                        index = i,
                        title = item.mediaMetadata.title?.toString()
                            ?: item.localConfiguration?.uri?.lastPathSegment
                            ?: "Track ${i + 1}",
                        isCurrent = i == currentIndex,
                    )
                )
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Queue", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${itemCount} tracks", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            ) {
                itemsIndexed(items, key = { _, item -> item.index }) { _, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                player.seekTo(item.index, 0)
                                player.play()
                            }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (item.isCurrent) {
                            Icon(
                                painter = painterResource(coreUiR.drawable.ic_play),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        } else {
                            Spacer(modifier = Modifier.width(28.dp))
                        }
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (item.isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (item.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private data class QueueItem(val index: Int, val title: String, val isCurrent: Boolean)

// ── Dedicated Audio Player UI ────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    player: Player?,
    title: String,
    artist: String,
    uri: Uri?,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current

    // Show loading state if player is not yet connected
    if (player == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            MaterialTheme.colorScheme.surface,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Connecting to player...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        return
    }

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

    // Queue and Settings state
    var showQueue by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Current track info (updates when track changes)
    var currentTitle by remember { mutableStateOf(title) }
    var currentArtist by remember { mutableStateOf(artist) }
    var currentUri by remember { mutableStateOf(uri) }

    // Rotate album art when playing — use infinite transition only when playing
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 20_000), RepeatMode.Restart),
        label = "vinylRotation",
    )
    // Track the last rotation angle when pausing
    var savedRotation by remember { mutableFloatStateOf(0f) }
    val displayRotation = if (isPlaying) {
        savedRotation = rotation
        rotation
    } else {
        savedRotation
    }

    // Poll player state every 200ms
    LaunchedEffect(player) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) { isPlaying = playing }
            override fun onPlaybackStateChanged(state: Int) {
                duration = player.duration.coerceAtLeast(0)
            }
            override fun onMediaItemTransition(mediaItem: androidx.media3.common.MediaItem?, reason: Int) {
                mediaItem?.let {
                    currentTitle = it.mediaMetadata.title?.toString() ?: it.localConfiguration?.uri?.lastPathSegment ?: "Unknown"
                    currentArtist = it.mediaMetadata.artist?.toString() ?: "Unknown Artist"
                    currentUri = it.localConfiguration?.uri
                    // Re-extract album art for new track
                    val newUri = it.localConfiguration?.uri
                    if (newUri != null) {
                        // Album art will be refreshed by LaunchedEffect(uri) above
                        // when currentUri changes via recomposition
                    } else {
                        albumArt = null
                    }
                }
            }
        }
        player.addListener(listener)
        while (isActive) {
            if (!isSeeking) position = player.currentPosition.coerceAtLeast(0)
            duration = player.duration.coerceAtLeast(0)
            // Update current track info from player if available
            player.currentMediaItem?.let {
                currentTitle = it.mediaMetadata.title?.toString() ?: currentTitle
                currentArtist = it.mediaMetadata.artist?.toString() ?: currentArtist
            }
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
                // Queue button
                IconButton(onClick = { showQueue = true }) {
                    Icon(
                        imageVector = NextIcons.DashBoard,
                        contentDescription = "Queue",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
                // Settings button
                IconButton(onClick = { showSettings = true }) {
                    Icon(
                        imageVector = NextIcons.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Album art (vinyl record style) ────────────────────────────
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                // Audio visualizer behind album art
                AudioVisualizerView(
                    player = player,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                )

                if (albumArt != null) {
                    Image(
                        bitmap = albumArt!!.asImageBitmap(),
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(displayRotation),
                    )
                } else {
                    // Default vinyl disc icon
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_music_note),
                        contentDescription = "Music",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(96.dp)
                            .rotate(displayRotation),
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
                currentTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                currentArtist,
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

            // ── Shuffle + Repeat toggle row ──────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Shuffle toggle — random playback order
                IconButton(
                    onClick = { player.shuffleModeEnabled = !player.shuffleModeEnabled },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(coreUiR.drawable.ic_shuffle),
                        contentDescription = "Shuffle",
                        tint = if (player.shuffleModeEnabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp),
                    )
                }

                // Repeat toggle — cycles OFF → ALL → ONE → OFF
                IconButton(
                    onClick = {
                        player.repeatMode = when (player.repeatMode) {
                            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
                            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
                            Player.REPEAT_MODE_ONE -> Player.REPEAT_MODE_OFF
                            else -> Player.REPEAT_MODE_OFF
                        }
                    },
                    modifier = Modifier.size(48.dp),
                ) {
                    val repeatIcon = when (player.repeatMode) {
                        Player.REPEAT_MODE_ONE -> coreUiR.drawable.ic_loop_one
                        Player.REPEAT_MODE_ALL -> coreUiR.drawable.ic_loop_all
                        else -> coreUiR.drawable.ic_loop_off
                    }
                    val repeatTint = if (player.repeatMode != Player.REPEAT_MODE_OFF)
                        MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                    Icon(
                        painter = painterResource(repeatIcon),
                        contentDescription = "Repeat",
                        tint = repeatTint,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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

    // Queue bottom sheet
    if (showQueue) {
        QueueBottomSheet(player = player, onDismiss = { showQueue = false })
    }

    // Settings dialog
    if (showSettings) {
        AudioSettingsDialog(player = player, onDismiss = { showSettings = false })
    }
}
