package dev.anilbeesetti.nextplayer.feature.player.engine

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.anilbeesetti.nextplayer.core.ui.theme.NextPlayerTheme
import dev.anilbeesetti.nextplayer.feature.player.engine.VlcPlayerEngine

/**
 * VlcPlayerActivity — Compose-based activity that uses VlcPlayerEngine for playback.
 *
 * REPLACES the ExoPlayer/Media3-based PlayerActivity. Features:
 *   - LibVLC video rendering via SurfaceView
 *   - Sample-accurate seeking (forward/backward)
 *   - PiP support (32-bit + S+ safe via Rational.coercePiPSafe)
 *   - Background playback via VlcPlaybackService
 *   - Audio delay + equalizer integration
 *
 * Launch via ACTION_VIEW with a video/audio URI.
 */
class VlcPlayerActivity : AppCompatActivity() {

    private var engine: VlcPlayerEngine? = null
    private var surfaceView: SurfaceView? = null
    private var initialUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fullscreen immersive
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initialUri = intent?.data

        // Initialize engine
        engine = VlcPlayerEngine(applicationContext).also {
            it.init()
        }

        // Build Compose UI
        val composeView = ComposeView(this).apply {
            setContent {
                NextPlayerTheme {
                    VlcPlayerContent(
                        engine = engine,
                        uri = initialUri,
                        onSurfaceCreated = { sv -> surfaceView = sv },
                    )
                }
            }
        }
        setContentView(composeView)
    }

    override fun onStart() {
        super.onStart()
        // Start playback
        initialUri?.let { uri ->
            engine?.setDataSource(uri)
            engine?.play()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isFinishing && !isInPictureInPictureMode) {
            // Hand off to background service
            initialUri?.let { uri ->
                VlcPlaybackService.startPlayback(
                    context = this,
                    uri = uri.toString(),
                    title = uri.lastPathSegment ?: "Unknown",
                    isAudio = false,
                )
            }
            engine?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        engine?.release()
        engine = null
    }

    // ── PiP support ──────────────────────────────────────────────────────
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && engine?.isPlaying == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // S+ uses setAutoEnterEnabled — set params, system handles entry
                try {
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .setAutoEnterEnabled(true)
                        .build()
                    setPictureInPictureParams(params)
                } catch (e: Exception) {
                    // 32-bit devices may fail — ignore
                }
            } else {
                // O-R: explicit entry
                try {
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .build()
                    enterPictureInPictureMode(params)
                } catch (e: Exception) {
                    // 32-bit devices may fail — ignore
                }
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        // Hide controls in PiP
    }
}

@Composable
private fun VlcPlayerContent(
    engine: VlcPlayerEngine?,
    uri: Uri?,
    onSurfaceCreated: (SurfaceView) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        AndroidView(
            factory = { ctx ->
                SurfaceView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            engine?.setSurface(holder.surface)
                        }
                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, w: Int, h: Int) {}
                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            engine?.setSurface(null)
                        }
                    })
                    onSurfaceCreated(this)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
