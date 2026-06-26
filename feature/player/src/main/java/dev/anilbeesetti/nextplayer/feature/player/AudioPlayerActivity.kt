package dev.anilbeesetti.nextplayer.feature.player

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import dev.anilbeesetti.nextplayer.core.ui.theme.NextPlayerTheme
import dev.anilbeesetti.nextplayer.feature.player.service.PlayerService
import dev.anilbeesetti.nextplayer.feature.player.service.stopPlayerSession
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch

/**
 * Dedicated activity for audio-only playback.
 * Shows album art + standard audio controls — NO black video surface.
 *
 * v0.16.2 — bulletproofing against the v0.16.0/v0.16.1 crash reports:
 *   1. setContent() is ALWAYS called, even on early-return paths. Android
 *      throws "Activity did not call setContent()" if you finish() before
 *      setContent() — this was a likely silent crash before.
 *   2. Wrap bindPlayerService in try-catch with Toast on failure — if
 *      PlayerService binding fails for any reason, show a toast and finish
 *      instead of leaving the user on a perpetual "Connecting to player..."
 *      spinner.
 *   3. Use applicationContext for SessionToken (matches upstream NextPlayer
 *      pattern in PlayerActivity.maybeInitControllerFuture). Using the
 *      activity context can cause ServiceConnectionLeaked on some ROMs.
 *   4. Defensive null check on intent.data — if launched without a URI,
 *      show a toast and finish gracefully.
 *   5. onNewIntent handles re-launch (singleTask) — if user is already in
 *      AudioPlayerActivity and taps another file, replace queue atomically.
 */
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@AndroidEntryPoint
class AudioPlayerActivity : ComponentActivity() {

    companion object {
        private const val TAG = "AudioPlayerActivity"
        const val EXTRA_TITLE = "title"
        const val EXTRA_ARTIST = "artist"
        const val EXTRA_QUEUE = "audio_queue"
        const val EXTRA_QUEUE_INDEX = "audio_queue_index"
    }

    private var controllerFuture: ListenableFuture<MediaController>? = null

    /**
     * MediaController wrapped in mutableStateOf so that Compose UI recomposes
     * automatically when the controller is connected. Without this, the
     * AudioPlayerScreen stays stuck on "Connecting to player..." even after
     * the background service starts playback.
     */
    private val mediaControllerState = androidx.compose.runtime.mutableStateOf<MediaController?>(null)
    private val mediaController: MediaController? get() = mediaControllerState.value

    /**
     * Tracks whether the MediaController is currently bound to the PlayerService.
     */
    private var isBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        // Parse intent — extract URI, title, artist, queue.
        // If URI is missing, show a friendly toast and finish. CRITICAL:
        // we still call setContent{} first so Android doesn't crash with
        // "Activity did not call setContent()".
        val uri: Uri? = intent.data
        val title = intent.getStringExtra(EXTRA_TITLE) ?: uri?.lastPathSegment ?: "Unknown"
        val artist = intent.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"
        val startIndex = intent.getIntExtra(EXTRA_QUEUE_INDEX, 0)
        @Suppress("DEPRECATION")
        val queue: ArrayList<Uri> = if (uri != null) {
            intent.getParcelableArrayListExtra<Uri>(EXTRA_QUEUE) ?: arrayListOf(uri)
        } else {
            arrayListOf()
        }

        // ALWAYS call setContent first — even if we're going to finish.
        setContent {
            NextPlayerTheme(darkTheme = true) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AudioPlayerScreen(
                        player = mediaController,
                        title = title,
                        artist = artist,
                        uri = uri,
                        onBackClick = { finish() },
                    )
                }
            }
        }

        if (uri == null) {
            Log.e(TAG, "onCreate: intent.data is null, finishing")
            Toast.makeText(this, "No audio URL provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Bind to PlayerService — wrapped in try-catch so any binding failure
        // shows a user-visible toast instead of crashing the activity.
        try {
            bindPlayerService(uri, title, artist, queue, startIndex)
        } catch (t: Throwable) {
            Log.e(TAG, "bindPlayerService threw", t)
            Toast.makeText(this, "Failed to start audio player: ${t.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // SingleTask launch mode: user tapped a new file while AudioPlayerActivity
        // was still alive. Replace the queue atomically.
        val newUri: Uri = intent.data ?: return
        val newTitle = intent.getStringExtra(EXTRA_TITLE) ?: newUri.lastPathSegment ?: "Unknown"
        val newArtist = intent.getStringExtra(EXTRA_ARTIST) ?: "Unknown Artist"
        val startIndex = intent.getIntExtra(EXTRA_QUEUE_INDEX, 0)
        @Suppress("DEPRECATION")
        val queue: ArrayList<Uri> = intent.getParcelableArrayListExtra<Uri>(EXTRA_QUEUE)
            ?: arrayListOf(newUri)

        // Stop the current playback first, then re-bind with the new queue.
        lifecycleScope.launch {
            try {
                mediaController?.let { old ->
                    runCatching {
                        old.stop()
                        old.clearMediaItems()
                    }
                }
                startPlaybackOnController(newUri, newTitle, newArtist, queue, startIndex)
            } catch (t: Throwable) {
                Log.e(TAG, "onNewIntent: replace queue failed", t)
            }
        }
    }

    /**
     * Binds to the PlayerService via MediaController.Builder.buildAsync().
     *
     * v0.16.2 — uses applicationContext for SessionToken (matches upstream
     * PlayerActivity pattern). Hard-stops any existing session before
     * queueing new media so back-press → new-file doesn't keep File A
     * playing in the background.
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
                // 1. If a previous controller is still bound, stop it first.
                mediaController?.let { old ->
                    runCatching {
                        old.stopPlayerSession()
                        old.stop()
                        old.clearMediaItems()
                    }
                }

                // 2. Build a new controller using applicationContext (not activity context).
                val token = SessionToken(
                    applicationContext,
                    ComponentName(applicationContext, PlayerService::class.java),
                )
                controllerFuture = MediaController.Builder(applicationContext, token).buildAsync()
                val controller = controllerFuture!!.await()
                mediaControllerState.value = controller
                isBound = true

                Log.d(TAG, "MediaController bound successfully, isBound=true")

                startPlaybackOnController(uri, title, artist, queue, startIndex)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to bind MediaController", e)
                isBound = false
                runOnUiThread {
                    Toast.makeText(
                        this@AudioPlayerActivity,
                        "Could not connect to audio service: ${e.message}",
                        Toast.LENGTH_LONG,
                    ).show()
                    finish()
                }
            }
        }
    }

    /**
     * Atomically replaces the queue with the new media items and starts playback.
     * Called from both onCreate (first launch) and onNewIntent (subsequent).
     */
    private fun startPlaybackOnController(
        uri: Uri,
        title: String,
        artist: String,
        queue: ArrayList<Uri>,
        startIndex: Int,
    ) {
        val controller = mediaController ?: run {
            Log.w(TAG, "startPlaybackOnController: controller is null")
            return
        }
        try {
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

            // Hard-stop + clear before queuing the new media — atomic queue replace.
            runCatching {
                controller.stop()
                controller.clearMediaItems()
            }

            controller.setMediaItems(mediaItems, startIndex, 0L)
            controller.playWhenReady = true
            controller.prepare()
            controller.play()
            Log.d(TAG, "Playback started: $title (queue size=${mediaItems.size})")
        } catch (e: Exception) {
            Log.e(TAG, "startPlaybackOnController failed", e)
        }
    }

    /**
     * Safely releases the MediaController.
     */
    private fun safelyReleaseController() {
        if (!isBound) {
            Log.d(TAG, "safelyReleaseController: not bound, skipping release")
            return
        }
        try {
            mediaController?.let { controller ->
                try {
                    runCatching { if (controller.isPlaying) controller.pause() }
                    controller.release()
                    Log.d(TAG, "MediaController released successfully")
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "IllegalArgumentException during controller release", e)
                } catch (e: Exception) {
                    Log.w(TAG, "Exception during controller release", e)
                }
            }
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Service not registered during release", e)
        } finally {
            mediaControllerState.value = null
            isBound = false
        }
        try {
            controllerFuture?.cancel(true)
        } catch (e: Exception) {
            Log.w(TAG, "Exception during future cancel", e)
        } finally {
            controllerFuture = null
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        if (isFinishing) {
            // v0.16.2 — back-press stops audio entirely.
            mediaController?.let { controller ->
                runCatching {
                    controller.pause()
                    controller.stop()
                    controller.clearMediaItems()
                    controller.stopPlayerSession()
                }
            }
            safelyReleaseController()
        }
        super.onStop()
    }

    override fun onDestroy() {
        safelyReleaseController()
        super.onDestroy()
    }
}
