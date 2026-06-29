package dev.anilbeesetti.nextplayer.feature.player.engine

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.anilbeesetti.nextplayer.core.ui.theme.NextPlayerTheme
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * VlcPlayerActivity — LibVLC-powered primary video player (default from v0.19.0+).
 *
 * PlayerActivity.onCreate() forwards all video intents here. ExoPlayer/Media3 is
 * no longer the primary playback path — LibVLC handles all video rendering.
 *
 * LibVLC advantages over ExoPlayer for this use case:
 *  - Native sample-accurate seeking across all containers (MKV, MP4, AVI, TS, etc.)
 *  - Hardware decoder with automatic software fallback
 *  - Native audio delay API (μs precision, no ExoPlayer audio processor hacks)
 *  - Built-in subtitle rendering (SRT, ASS, SSA, embedded MKV tracks)
 *  - Network stream support (HTTP, RTSP, RTMP, UDP, MMS)
 *
 * UI features:
 *  - Horizontal drag → seek preview (release to apply, 2× sensitivity)
 *  - Vertical drag left half  → screen brightness (0-100%)
 *  - Vertical drag right half → system volume  (0-100%)
 *  - Double-tap left/right edge → ±10 s seek
 *  - Single tap → toggle controls overlay (auto-hides after 3.5 s)
 *  - Play/Pause centre button + seek bar + elapsed/total time
 *  - Audio Delay dialog: ±3000 ms, ±50 ms fine steps, native VLC μs API
 *  - PiP: auto-enter on Android S+, explicit entry on O-R
 *  - Screen kept awake while playing
 */
class VlcPlayerActivity : AppCompatActivity() {

    private var engine: VlcPlayerEngine? = null
    private var uri: Uri? = null
    private var videoTitle: String = ""
    private var hasStartedPlayback = false

    // Mutable state observed by Compose
    private val isPlayingState  = mutableStateOf(false)
    private val positionMs      = mutableLongStateOf(0L)
    private val durationMs      = mutableLongStateOf(0L)
    private val isBufferingState = mutableStateOf(false)

    private val engineListener = object : VlcPlayerEngine.EventListener {
        override fun onPlaying() {
            isPlayingState.value = true
            isBufferingState.value = false
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        override fun onPaused() {
            isPlayingState.value = false
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        override fun onStopped()  { isPlayingState.value = false }
        override fun onEndReached() { finish() }
        override fun onError(message: String?) { finish() }
        override fun onBuffering(percent: Float) {
            isBufferingState.value = percent in 1f..99f
        }
        override fun onTimeChanged(timeMs: Long)   { positionMs.longValue = timeMs }
        override fun onLengthChanged(lengthMs: Long) { durationMs.longValue = lengthMs }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Immersive fullscreen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).also { ic ->
            ic.hide(WindowInsetsCompat.Type.systemBars())
            ic.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        uri = intent.data
        videoTitle = uri?.lastPathSegment?.let { seg ->
            runCatching { java.net.URLDecoder.decode(seg, "UTF-8") }.getOrElse { seg }
                .substringBeforeLast(".")
        } ?: ""

        // Init LibVLC engine
        engine = VlcPlayerEngine(applicationContext).also { eng ->
            eng.init()
            eng.addListener(engineListener)
        }

        // PiP auto-enter on Android S+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            runCatching {
                setPictureInPictureParams(
                    PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .setAutoEnterEnabled(true)
                        .build()
                )
            }
        }

        val composeView = ComposeView(this).apply {
            setContent {
                NextPlayerTheme {
                    VlcPlayerScreen(
                        title       = videoTitle,
                        isPlaying   = isPlayingState.value,
                        position    = positionMs.longValue,
                        duration    = durationMs.longValue,
                        isBuffering = isBufferingState.value,
                        onSurfaceReady   = { surface -> engine?.setSurface(surface) },
                        onSurfaceLost    = { engine?.setSurface(null) },
                        onPlay           = { engine?.play() },
                        onPause          = { engine?.pause() },
                        onSeekTo         = { ms -> engine?.seekTo(ms) },
                        onSeekRelative   = { delta -> engine?.seekRelative(delta) },
                        onSetVolume      = { vol ->
                            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
                            am.setStreamVolume(AudioManager.STREAM_MUSIC, vol, 0)
                        },
                        onSetBrightness  = { br ->
                            val attrs = window.attributes
                            attrs.screenBrightness = br
                            window.attributes = attrs
                        },
                        onSetAudioDelay  = { ms -> engine?.setAudioDelay(ms) },
                        onBack           = { finish() },
                    )
                }
            }
        }
        setContentView(composeView)
    }

    override fun onStart() {
        super.onStart()
        if (!hasStartedPlayback) {
            hasStartedPlayback = true
            if (uri != null) {
                engine?.setDataSource(uri!!)
                engine?.play()
            } else {
                finish()
            }
        } else if (!isInPictureInPictureMode) {
            engine?.play()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isInPictureInPictureMode) {
            engine?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        engine?.removeListener(engineListener)
        engine?.release()
        engine = null
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipIfPossible()
    }

    override fun onPictureInPictureModeChanged(isInPiP: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPiP, newConfig)
        // Controls auto-hidden in PiP; no extra work needed
    }

    private fun enterPipIfPossible() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (engine?.isPlaying != true) return
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) return // auto-enter already configured
        runCatching {
            enterPictureInPictureMode(
                PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9)).build()
            )
        }
    }
}

// ─── Gesture type ─────────────────────────────────────────────────────────────

private enum class GestureType { NONE, SEEK, VOLUME, BRIGHTNESS }

// ─── Time formatter ───────────────────────────────────────────────────────────

private fun Long.toTimeString(): String {
    val total = (this / 1000L).coerceAtLeast(0L)
    val h = total / 3600L
    val m = (total % 3600L) / 60L
    val s = total % 60L
    return if (h > 0L) "%d:%02d:%02d".format(h, m, s) else "%02d:%02d".format(m, s)
}

// ─── Main player screen ───────────────────────────────────────────────────────

@Composable
private fun VlcPlayerScreen(
    title: String,
    isPlaying: Boolean,
    position: Long,
    duration: Long,
    isBuffering: Boolean,
    onSurfaceReady: (android.view.Surface) -> Unit,
    onSurfaceLost: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onSeekRelative: (Long) -> Unit,
    onSetVolume: (Int) -> Unit,
    onSetBrightness: (Float) -> Unit,
    onSetAudioDelay: (Long) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVol = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }

    // Controls visibility + auto-hide
    var controlsVisible by remember { mutableStateOf(true) }
    var controlsRevealKey by remember { mutableIntStateOf(0) }
    LaunchedEffect(controlsRevealKey) {
        if (controlsVisible) {
            delay(3500)
            controlsVisible = false
        }
    }

    // Gesture tracking
    var gestureType by remember { mutableStateOf(GestureType.NONE) }
    var seekDragMs  by remember { mutableLongStateOf(0L) }
    var seekAnchorMs by remember { mutableLongStateOf(0L) }
    var volPercent  by remember { mutableIntStateOf(
        if (maxVol > 0) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / maxVol else 50
    ) }
    var brightPercent by remember { mutableIntStateOf(50) }

    // Audio delay
    var showDelayDialog    by remember { mutableStateOf(false) }
    var appliedDelayMs     by remember { mutableLongStateOf(0L) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {

        // ── 1. Video surface ──────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(h: SurfaceHolder)  { onSurfaceReady(h.surface) }
                        override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, ht: Int) {}
                        override fun surfaceDestroyed(h: SurfaceHolder) { onSurfaceLost() }
                    })
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // ── 2. Buffering spinner ──────────────────────────────────────────────
        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
                strokeWidth = 3.dp,
            )
        }

        // ── 3. Gesture overlay ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Tap: show/hide controls; double-tap: ±10 s seek
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            controlsVisible = !controlsVisible
                            controlsRevealKey++
                        },
                        onDoubleTap = { offset ->
                            if (offset.x < size.width / 2f) onSeekRelative(-10_000L)
                            else onSeekRelative(10_000L)
                            controlsVisible = true
                            controlsRevealKey++
                        },
                    )
                }
                // Drag: seek (horizontal) / brightness (left vertical) / volume (right vertical)
                .pointerInput(duration) {
                    var startX = 0f
                    var startY = 0f
                    var accumulated = Offset.Zero
                    var gType = GestureType.NONE
                    var seekBase = 0L
                    var volBase = 0
                    var brightBase = 0f

                    detectDragGestures(
                        onDragStart = { offset ->
                            startX = offset.x
                            startY = offset.y
                            accumulated = Offset.Zero
                            gType = GestureType.NONE
                            seekBase = position.coerceAtLeast(0L)
                            seekAnchorMs = seekBase
                            seekDragMs   = seekBase
                            volBase   = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                            brightBase = brightPercent / 100f
                        },
                        onDrag = { _, delta ->
                            accumulated += delta
                            // Determine gesture direction after threshold
                            if (gType == GestureType.NONE) {
                                val ax = abs(accumulated.x)
                                val ay = abs(accumulated.y)
                                if (ax > 14f || ay > 14f) {
                                    gType = when {
                                        ax > ay * 1.5f -> GestureType.SEEK
                                        ay > ax * 1.5f ->
                                            if (startX < size.width / 2f) GestureType.BRIGHTNESS
                                            else GestureType.VOLUME
                                        else -> GestureType.NONE
                                    }
                                }
                            }
                            gestureType = gType
                            val safeDur = duration.coerceAtLeast(1L)
                            when (gType) {
                                GestureType.SEEK -> {
                                    // 2× sensitivity: full screen width = 2× duration
                                    val deltaMs = (accumulated.x * safeDur * 2L / size.width).toLong()
                                    seekDragMs = (seekBase + deltaMs).coerceIn(0L, safeDur)
                                }
                                GestureType.VOLUME -> {
                                    val dy = -(accumulated.y / size.height)
                                    val newVol = (volBase + (dy * maxVol * 1.5f).toInt()).coerceIn(0, maxVol)
                                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, 0)
                                    volPercent = if (maxVol > 0) newVol * 100 / maxVol else 0
                                    onSetVolume(newVol)
                                }
                                GestureType.BRIGHTNESS -> {
                                    val dy = -(accumulated.y / size.height)
                                    val newBr = (brightBase + dy).coerceIn(0.01f, 1f)
                                    brightPercent = (newBr * 100).toInt()
                                    onSetBrightness(newBr)
                                }
                                GestureType.NONE -> {}
                            }
                        },
                        onDragEnd = {
                            if (gType == GestureType.SEEK && duration > 0L) onSeekTo(seekDragMs)
                            gType = GestureType.NONE
                            gestureType = GestureType.NONE
                        },
                        onDragCancel = {
                            gType = GestureType.NONE
                            gestureType = GestureType.NONE
                        },
                    )
                },
        )

        // ── 4. Seek drag badge (centre) ───────────────────────────────────────
        if (gestureType == GestureType.SEEK) {
            val deltaMs = seekDragMs - seekAnchorMs
            Surface(
                modifier = Modifier.align(Alignment.Center),
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.78f),
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "${if (deltaMs >= 0L) "+" else ""}${deltaMs / 1000L}s",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = seekDragMs.toTimeString(),
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 16.sp,
                    )
                }
            }
        }

        // ── 5. Volume badge (right) ───────────────────────────────────────────
        AnimatedVisibility(
            visible = gestureType == GestureType.VOLUME,
            enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp),
        ) { GestureBadge("Vol", volPercent) }

        // ── 6. Brightness badge (left) ────────────────────────────────────────
        AnimatedVisibility(
            visible = gestureType == GestureType.BRIGHTNESS,
            enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp),
        ) { GestureBadge("Bri", brightPercent) }

        // ── 7. Controls overlay ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = controlsVisible && gestureType == GestureType.NONE,
            enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
            ) {
                // Top bar: back + title + audio-delay button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onBack) {
                        Text("←", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    TextButton(onClick = { showDelayDialog = true }) {
                        Text("Audio Delay", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    }
                }

                // Centre: Play / Pause
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.55f),
                    onClick = { if (isPlaying) onPause() else onPlay() },
                ) {
                    Text(
                        text = if (isPlaying) "⏸" else "▶",
                        modifier = Modifier.padding(horizontal = 28.dp, vertical = 20.dp),
                        color = Color.White,
                        fontSize = 36.sp,
                    )
                }

                // Bottom: position + seek bar + duration
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(position.toTimeString(), color = Color.White, fontSize = 13.sp)
                        Text(duration.toTimeString(), color = Color.White.copy(alpha = 0.65f), fontSize = 13.sp)
                    }
                    Slider(
                        value = if (duration > 0L) (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f,
                        onValueChange = { f -> onSeekTo((f * duration).toLong()) },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.28f),
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        // ── 8. Audio Delay Dialog ─────────────────────────────────────────────
        if (showDelayDialog) {
            var tempDelay by remember { mutableLongStateOf(appliedDelayMs) }
            AlertDialog(
                onDismissRequest = { showDelayDialog = false },
                title = { Text("Audio Sync / Delay") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text(
                            text = "Adjust audio offset relative to video.\n" +
                                "Positive = audio plays later (delays audio).\n" +
                                "Negative = audio plays earlier.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "${if (tempDelay >= 0L) "+" else ""}$tempDelay ms",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Slider(
                            value = tempDelay.toFloat(),
                            onValueChange = { tempDelay = it.toLong() },
                            valueRange = -3000f..3000f,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedButton(
                                onClick = { tempDelay = (tempDelay - 50L).coerceAtLeast(-3000L) },
                                modifier = Modifier.weight(1f),
                            ) { Text("-50ms") }
                            OutlinedButton(
                                onClick = { tempDelay = 0L },
                                modifier = Modifier.weight(1f),
                            ) { Text("Reset") }
                            OutlinedButton(
                                onClick = { tempDelay = (tempDelay + 50L).coerceAtMost(3000L) },
                                modifier = Modifier.weight(1f),
                            ) { Text("+50ms") }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        appliedDelayMs = tempDelay
                        onSetAudioDelay(tempDelay)
                        showDelayDialog = false
                    }) { Text("Apply") }
                },
                dismissButton = {
                    TextButton(onClick = { showDelayDialog = false }) { Text("Cancel") }
                },
            )
        }
    }
}

// ─── Gesture indicator badge ──────────────────────────────────────────────────

@Composable
private fun GestureBadge(label: String, percent: Int) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color.Black.copy(alpha = 0.75f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            Text("$percent%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
    }
}
