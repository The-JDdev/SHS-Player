package dev.anilbeesetti.nextplayer.feature.player

import android.content.ComponentName
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import dev.anilbeesetti.nextplayer.core.ui.theme.NextPlayerTheme
import dev.anilbeesetti.nextplayer.feature.player.service.PlayerService
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

/**
 * Dedicated activity for audio-only playback.
 * Shows album art + standard audio controls — NO black video surface.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@AndroidEntryPoint
class AudioPlayerActivity : ComponentActivity() {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        val uri = intent.data ?: run { finish(); return }
        val title = intent.getStringExtra("title") ?: uri.lastPathSegment ?: "Unknown"
        val artist = intent.getStringExtra("artist") ?: "Unknown Artist"
        val startIndex = intent.getIntExtra("audio_queue_index", 0)
        @Suppress("DEPRECATION")
        val queue = intent.getParcelableArrayListExtra<Uri>("audio_queue") ?: arrayListOf(uri)

        setContent {
            NextPlayerTheme(darkTheme = true) {
                val player = mediaController
                if (player != null) {
                    AudioPlayerScreen(
                        player = player,
                        title = title,
                        artist = artist,
                        uri = uri,
                        onBackClick = { finish() },
                    )
                }
            }
        }

        lifecycleScope.launch {
            val token = SessionToken(this@AudioPlayerActivity,
                ComponentName(this@AudioPlayerActivity, PlayerService::class.java))
            controllerFuture = MediaController.Builder(this@AudioPlayerActivity, token).buildAsync()
            val controller = controllerFuture!!.await()
            mediaController = controller

            val mediaItems = queue.mapIndexed { index, itemUri ->
                MediaItem.Builder()
                    .setUri(itemUri)
                    .setMediaId(itemUri.toString())
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(
                                if (index == startIndex) title
                                else itemUri.lastPathSegment ?: "Track ${index + 1}"
                            )
                            .setArtist(
                                if (index == startIndex) artist
                                else "Unknown Artist"
                            )
                            .build(),
                    )
                    .build()
            }

            controller.setMediaItems(mediaItems, startIndex, 0L)
            controller.playWhenReady = true
            controller.prepare()

            // Trigger recomposition by recreating content
            setContent {
                NextPlayerTheme(darkTheme = true) {
                    AudioPlayerScreen(
                        player = controller,
                        title = title,
                        artist = artist,
                        uri = uri,
                        onBackClick = { finish() },
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        mediaController?.release()
        controllerFuture?.cancel(true)
        super.onDestroy()
    }
}
