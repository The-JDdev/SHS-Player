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
 * Why this exists:
 *   - Some codecs (HEVC 10-bit, AV1, certain MKV variants) seek poorly on ExoPlayer.
 *   - LibVLC's seeking is sample-accurate across all containers.
 *   - LibVLC exposes [setAudioDelay] and a real [Equalizer] natively, which ExoPlayer lacks.
 *
 * Usage:
 *   val engine = VlcEngine(context)
 *   engine.setSurface(surface)
 *   engine.setDataSource(uri)
 *   engine.play()
 *   engine.setAudioDelay(150)  // 150ms audio delay
 *   engine.setAudioEqualizerBands(...)  // 10-band EQ
 *
 * Lifecycle:
 *   - Call [release] when done — LibVLC holds native resources.
 *   - Surface can be attached/detached without recreating the engine.
 *
 * NOTE: This is wired in as an alternative engine path, used when the user picks
 * "VLC" in Settings → Player → Decoder. ExoPlayer remains the default for
 * Media3 MediaSession integration (notification, PiP, background playback).
 */
class VlcEngine(private val context: Context) {

    private var libVlc: LibVLC? = null
    private var mediaPlayer: MediaPlayer? = null
    private var equalizer: MediaPlayer.Equalizer? = null
    private var currentUri: Uri? = null

    var audioDelayMs: Long = 0L
        private set

    fun init() {
        if (libVlc != null) return
        val options = buildList {
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
        if (surface != null) {
            if (!vout.areViewsCreated()) {
                vout.setVideoSurface(surface, null)
                vout.attachViews()
            }
        } else {
            if (vout.areViewsCreated()) vout.detachViews()
        }
    }

    fun setDataSource(uri: Uri) {
        val mp = mediaPlayer ?: return
        currentUri = uri
        val media = Media(libVlc!!, uri).apply {
            setHWDecoderEnabled(true, false)
        }
        mp.media = media
        media.release()
    }

    fun play() { mediaPlayer?.play() }
    fun pause() { mediaPlayer?.pause() }
    fun stop() { mediaPlayer?.stop() }

    fun seekTo(positionMs: Long) { mediaPlayer?.time = positionMs }
    fun setVolume(volume: Int) { mediaPlayer?.volume = volume.coerceIn(0, 100) }
    fun setRate(rate: Float) { mediaPlayer?.rate = rate }

    /**
     * Set audio delay in milliseconds.
     * Positive = audio plays later (audio lags video).
     * Negative = audio plays earlier.
     * LibVLC applies this at the audio output level — sample-accurate.
     */
    fun setAudioDelay(delayMs: Long) {
        audioDelayMs = delayMs
        mediaPlayer?.let { runCatching { it.setAudioDelay(delayMs * 1000) } }
    }

    /**
     * Apply LibVLC audio equalizer (10-band).
     * @param bands list of band gains in dB, must be 10 entries for LibVLC's standard bands
     *   (32Hz, 64Hz, 125Hz, 250Hz, 500Hz, 1kHz, 2kHz, 4kHz, 8kHz, 16kHz).
     * @param preAmp pre-amplifier gain in dB.
     */
    fun setAudioEqualizerBands(bands: FloatArray, preAmp: Float = 0f) {
        val mp = mediaPlayer ?: return
        runCatching {
            val eq = equalizer ?: MediaPlayer.Equalizer().also { equalizer = it }
            eq.setPreAmp(preAmp)
            for (i in bands.indices.coerceAtMost(eq.bandCount)) {
                eq.setAmp(i, bands[i])
            }
            mp.setEqualizer(eq)
        }.onFailure { Log.w("VlcEngine", "setAudioEqualizerBands failed", it) }
    }

    fun release() {
        runCatching { equalizer?.release() }
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
