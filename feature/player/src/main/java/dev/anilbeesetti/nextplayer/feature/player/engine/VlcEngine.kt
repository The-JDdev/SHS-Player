package dev.anilbeesetti.nextplayer.feature.player.engine

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Surface
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

/**
 * LibVLC engine wrapper — exposes the subset of player operations shs-player needs,
 * backed by LibVLC's native MediaPlayer.
 *
 * API surface ported from libvlc-android analysis (MediaPlayer.java):
 *   - new LibVLC(context, ArrayList<String>)            — LibVLC.java:43
 *   - new MediaPlayer(libVLC)                           — MediaPlayer.java:582
 *   - mediaPlayer.vlcVout.setVideoSurface(surface, h)   — IVLCVout (via getVLCVout)
 *   - mediaPlayer.vlcVout.attachViews() / detachViews()
 *   - mediaPlayer.time / setTime(ms)                    — MediaPlayer.java:1273 (ms)
 *   - mediaPlayer.volume / setVolume(int)               — MediaPlayer.java:1310
 *   - mediaPlayer.rate = float                          — MediaPlayer.java:1246
 *   - mediaPlayer.setAudioDelay(long microseconds)      — MediaPlayer.java:1113
 *   - MediaPlayer.Equalizer.create() + setPreAmp/setAmp — MediaPlayer.java:250 (nested class)
 *   - mediaPlayer.setEqualizer(eq)                      — MediaPlayer.java:1183
 *
 * Lifecycle:
 *   - Call [release] when done — LibVLC holds native resources.
 *   - Surface can be attached/detached without recreating the engine.
 *
 * NOTE: This is an alternative engine path. ExoPlayer remains the default for
 * Media3 MediaSession integration (notification, PiP, background playback).
 */
class VlcEngine(private val context: Context) {

    private var libVlc: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null
    private var equalizer: MediaPlayer.Equalizer? = null

    var audioDelayMs: Long = 0L
        private set

    fun init() {
        if (libVlc != null) return
        val options = ArrayList<String>().apply {
            add("--no-drop-late-frames")
            add("--no-skip-frames")
            add("--rtsp-tcp")
            add("--aout=opensles")
            add("--audio-time-stretch")
        }
        libVlc = LibVLC(context, options)
        mediaPlayer = MediaPlayer(libVlc!!)
    }

    fun setSurface(surface: Surface?) {
        val mp = mediaPlayer ?: return
        val vout = mp.vlcVout
        try {
            if (surface != null) {
                vout.setVideoSurface(surface, null)
                vout.attachViews()
            } else {
                vout.detachViews()
            }
        } catch (e: Exception) {
            Log.w(TAG, "setSurface failed", e)
        }
    }

    fun setDataSource(uri: Uri) {
        val mp = mediaPlayer ?: return
        val media = Media(libVlc!!, uri).apply {
            setHWDecoderEnabled(true, false)
        }
        mp.media = media
        media.release()
    }

    fun play() { mediaPlayer?.play() }
    fun pause() { mediaPlayer?.pause() }
    fun stop() { mediaPlayer?.stop() }

    fun seekTo(positionMs: Long) {
        mediaPlayer?.time = positionMs
    }

    fun setVolume(volume: Int) {
        mediaPlayer?.volume = volume.coerceIn(0, 100)
    }

    fun setRate(rate: Float) {
        mediaPlayer?.rate = rate
    }

    /**
     * Set audio delay in milliseconds.
     * Positive = audio plays later (audio lags video).
     * Negative = audio plays earlier.
     * LibVLC applies this at the audio output level — sample-accurate.
     * LibVLC's setAudioDelay takes MICROSECONDS (MediaPlayer.java:1113).
     */
    fun setAudioDelay(delayMs: Long) {
        audioDelayMs = delayMs
        mediaPlayer?.let { mp ->
            runCatching { mp.setAudioDelay(delayMs * 1000L) }
                .onFailure { Log.w(TAG, "setAudioDelay failed", it) }
        }
    }

    /**
     * Apply LibVLC audio equalizer (10-band).
     * @param bands list of band gains in dB. Pass an empty array to disable.
     * @param preAmp pre-amplifier gain in dB (-20..+20).
     */
    fun setAudioEqualizerBands(bands: FloatArray, preAmp: Float = 0f) {
        val mp = mediaPlayer ?: return
        if (bands.isEmpty()) {
            runCatching { mp.setEqualizer(null) }
            return
        }
        runCatching {
            val eq = equalizer ?: MediaPlayer.Equalizer.create().also { equalizer = it }
            eq.setPreAmp(preAmp.coerceIn(-20f, 20f))
            // LibVLC's standard band count is 10 (32Hz..16kHz). Clamp to bands.size.
            val count = minOf(bands.size, 10)
            for (i in 0 until count) {
                eq.setAmp(i, bands[i].coerceIn(-20f, 20f))
            }
            mp.setEqualizer(eq)
        }.onFailure { Log.w(TAG, "setAudioEqualizerBands failed", it) }
    }

    fun release() {
        // Note: MediaPlayer.Equalizer has no release() method per libvlc-android MediaPlayer.java
        // (its finalize() handles native cleanup). We null the reference and let GC handle it.
        equalizer = null
        runCatching { mediaPlayer?.release() }
        mediaPlayer = null
        runCatching { libVlc?.release() }
        libVlc = null
    }

    companion object {
        private const val TAG = "VlcEngine"
    }
}
