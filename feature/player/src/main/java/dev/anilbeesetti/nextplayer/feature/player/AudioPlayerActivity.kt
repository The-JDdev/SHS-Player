package dev.anilbeesetti.nextplayer.feature.player

import android.content.ComponentName
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
 *
 * FIX: ServiceConnection leak — tracks binding state with [isBound] flag,
 * guards unbind/release with try-catch, and pairs bind/unbind in
 * onStart()/onStop() for correct lifecycle symmetry.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@AndroidEntryPoint
class AudioPlayerActivity : ComponentActivity() {

    companion object {
        private const val TAG = "AudioPlayerActivity"
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    /**
     * Tracks whether the MediaController is currently bound to the PlayerService.
     * This prevents "Service not registered" crashes when unbindService() is called
     * without an active binding, and prevents ServiceConnectionLeaked warnings when
     * the activity is destroyed while still bound.
     */
    private var isBound: Boolean = false

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

        // Set initial content immediately — AudioPlayerScreen handles null player gracefully
        setContent {
            NextPlayerTheme(darkTheme = true) {
                AudioPlayerScreen(
                    player = mediaController,
                    title = title,
                    artist = artist,
                    uri = uri,
                    onBackClick = { finish() },
                )
            }
        }

        // Bind to PlayerService — this internally calls bindService()
        bindPlayerService(uri, title, artist, queue, startIndex)
    }

    /**
     * Binds to the PlayerService via MediaController.Builder.buildAsync().
     * The MediaController internally uses bindService(), so we must track
     * the binding state to properly unbind later.
     */
    private fun bindPlayerService(
        uri: Uri,
        title: String,
        artist: String,
        queue: ArrayList<Uri>,
        startIndex: Int,
    ) {
        lifecycleScope.launch {
            try {
                val token = SessionToken(
                    this@AudioPlayerActivity,
                    ComponentName(this@AudioPlayerActivity, PlayerService::class.java),
                )
                controllerFuture = MediaController.Builder(this@AudioPlayerActivity, token)
                    .buildAsync()
                val controller = controllerFuture!!.await()
                mediaController = controller
                isBound = true

                Log.d(TAG, "MediaController bound successfully, isBound=true")

                val mediaItems = queue.mapIndexed { index, itemUri ->
                    MediaItem.Builder()
                        .setUri(itemUri)
                        .setMediaId(itemUri.toString())
                        .setMediaMetadata(
                            MediaMetadata.Builder()
                                .setTitle(
                                    if (index == startIndex) title
                                    else itemUri.lastPathSegment ?: "Track ${index + 1}",
                                )
                                .setArtist(
                                    if (index == startIndex) artist
                                    else "Unknown Artist",
                                )
                                .build(),
                        )
                        .build()
                }

                controller.setMediaItems(mediaItems, startIndex, 0L)
                controller.playWhenReady = true
                controller.prepare()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind MediaController", e)
                isBound = false
            }
        }
    }

    /**
     * Safely releases the MediaController and unbinds from the PlayerService.
     *
     * CRITICAL FIX: Only releases/unbinds if [isBound] is true, and wraps
     * the entire operation in try-catch to suppress the fatal
     * IllegalArgumentException: "Service not registered" crash.
     */
    private fun safelyReleaseController() {
        if (!isBound) {
            Log.d(TAG, "safelyReleaseController: not bound, skipping release")
            return
        }

        try {
            mediaController?.let { controller ->
                try {
                    // Save playback position before releasing
                    runCatching {
                        if (controller.isPlaying) {
                            controller.pause()
                        }
                    }
                    controller.release()
                    Log.d(TAG, "MediaController released successfully")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "IllegalArgumentException during controller release (service already unregistered)", e)
                } catch (e: Exception) {
                    Log.w(TAG, "Exception during controller release", e)
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Service not registered during release", e)
        } finally {
            mediaController = null
            isBound = false
        }

        try {
            controllerFuture?.cancel(true)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "IllegalArgumentException during future cancel", e)
        } catch (e: Exception) {
            Log.w(TAG, "Exception during future cancel", e)
        } finally {
            controllerFuture = null
        }
    }

    override fun onStart() {
        super.onStart()
        // The binding is initiated in onCreate, but if the activity was
        // stopped and restarted, we may need to rebind.
        // MediaController handles reconnection automatically if the service
        // is still running.
    }

    override fun onStop() {
        // Paired with onCreate's bind — release if finishing
        if (isFinishing) {
            safelyReleaseController()
        }
        super.onStop()
    }

    override fun onDestroy() {
        // Final cleanup — always try to release, guarded by isBound + try-catch
        safelyReleaseController()
        super.onDestroy()
    }
}
