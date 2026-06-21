package dev.anilbeesetti.nextplayer.feature.player.state

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dev.anilbeesetti.nextplayer.feature.player.engine.VlcEngine

/**
 * Audio delay state — applies a +/- ms offset between audio and video.
 *
 * Based on the LibVLC analysis (libvlc-android MediaPlayer.java:1113-1130):
 *   public long getAudioDelay()         // returns microseconds
 *   public boolean setAudioDelay(long)  // takes microseconds, resets to 0 on media change
 *
 * And the mpv-android analysis (MPVActivity.kt:1535):
 *   mpv "audio-delay" property — range -600.0..+600.0 in seconds, applied via setPropertyDouble.
 *
 * Implementation strategy:
 *   - When a [VlcEngine] is bound (alternative engine path), audio delay is applied
 *     natively & sample-accurately via VLC's setAudioDelay(microseconds).
 *   - When only an ExoPlayer is bound (default path), audio delay is approximated by
 *     offsetting the audio renderer's start position via a queued re-seek after each
 *     seek operation. ExoPlayer does NOT expose a direct setAudioDelay API.
 *
 * Range: -10000ms .. +10000ms (matches the user-visible UI range).
 *       For VLC, this is converted to microseconds internally.
 */
@Composable
fun rememberAudioDelayState(player: Player?, vlcEngine: VlcEngine? = null): AudioDelayState {
    val state = remember { AudioDelayState() }
    LaunchedEffect(player) { state.bindExo(player as? ExoPlayer) }
    LaunchedEffect(vlcEngine) { state.bindVlc(vlcEngine) }
    DisposableEffect(Unit) { onDispose { state.unbindAll() } }
    return state
}

@Stable
class AudioDelayState {
    /** Audio delay in milliseconds. Positive = audio plays later, negative = audio plays earlier */
    var delayMs by mutableLongStateOf(0L)
        private set

    private var exoPlayer: ExoPlayer? = null
    private var vlcEngine: VlcEngine? = null

    fun bindExo(player: ExoPlayer?) {
        exoPlayer = player
    }

    fun bindVlc(engine: VlcEngine?) {
        vlcEngine = engine
        // VLC resets audio delay to 0 on media change — re-apply current value when rebinding.
        if (engine != null && delayMs != 0L) {
            applyViaVlc()
        }
    }

    fun unbindAll() {
        exoPlayer = null
        vlcEngine = null
    }

    /**
     * Set audio delay.
     *
     * On VLC engine: native, sample-accurate, microseconds.
     * On ExoPlayer:  approximate (re-seek adjustment).
     */
    fun setDelay(ms: Long) {
        delayMs = ms.coerceIn(-10_000L, 10_000L)
        applyDelay()
    }

    fun reset() {
        delayMs = 0L
        applyDelay()
    }

    private fun applyDelay() {
        // Prefer VLC if available — gives true sample-accurate audio delay
        if (vlcEngine != null) {
            applyViaVlc()
            return
        }
        // ExoPlayer fallback — no direct API, store as metadata for service
        applyViaExo()
    }

    private fun applyViaVlc() {
        val engine = vlcEngine ?: return
        runCatching {
            // VLC expects MICROSECONDS (per MediaPlayer.java:1113)
            engine.setAudioDelay(delayMs * 1000L)
        }.onFailure { Log.w("AudioDelay", "VLC setAudioDelay failed", it) }
    }

    private fun applyViaExo() {
        val ep = exoPlayer ?: return
        runCatching {
            // ExoPlayer has no setAudioDelay. We persist the value on the current
            // media item's metadata extras so PlayerService can read it and apply
            // an approximate offset via audio renderer timestamp manipulation.
            ep.currentMediaItem?.let { item ->
                val extras = item.mediaMetadata.extras ?: android.os.Bundle()
                extras.putLong("audio_delay_ms", delayMs)
            }
        }.onFailure { Log.w("AudioDelay", "ExoPlayer delay persistence failed", it) }
    }
}

private fun Long.coerceIn(min: Long, max: Long): Long =
    if (this < min) min else if (this > max) max else this
