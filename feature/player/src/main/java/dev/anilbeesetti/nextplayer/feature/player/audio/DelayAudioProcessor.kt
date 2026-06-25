package dev.anilbeesetti.nextplayer.feature.player.audio

import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.audio.BaseAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayDeque

/**
 * Phase 6.2 — Real, sample-accurate audio delay for ExoPlayer.
 *
 * Background: ExoPlayer has no `setAudioDelay(ms)` API. The video player's
 * audio-sync UI was previously a no-op (the delay value was written to media
 * metadata extras but never applied to the audio pipeline). This processor
 * finally hooks the UI slider to actual audio offset.
 *
 * How it works:
 *  - Positive `delayMs` → hold back N ms of audio before releasing it. Audio
 *    "plays later", which makes the video appear to lead.
 *  - Negative `delayMs` → skip N ms of audio at the start of each playback
 *    chunk so audio effectively plays earlier.
 *
 * Range: -10000 ms .. +10000 ms (matches the UI range).
 */
class DelayAudioProcessor : BaseAudioProcessor() {

    /**
     * Empty companion object — kept for any future @JvmStatic helpers.
     * (Previously had two companion objects which caused a compile error.)
     */
    companion object {
        private const val TAG = "DelayAudioProcessor"
        private val EMPTY_BUFFER: ByteBuffer = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder())
    }

    @Volatile
    private var requestedDelayMs: Long = 0L

    private var delayBytes: Long = 0L
    private var bytesToSkip: Long = 0L
    private var bytesToPad: Long = 0L

    private val inputBuffers: ArrayDeque<ByteBuffer> = ArrayDeque()
    private var currentOutput: ByteBuffer = EMPTY_BUFFER

    @Volatile
    var pendingAudioFormat: AudioProcessor.AudioFormat? = null
        private set

    override fun onConfigure(inputAudioFormat: AudioProcessor.AudioFormat): AudioProcessor.AudioFormat {
        if (inputAudioFormat.encoding != C.ENCODING_PCM_16BIT) {
            throw AudioProcessor.UnhandledAudioFormatException(inputAudioFormat)
        }
        pendingAudioFormat = inputAudioFormat
        recomputeDelayBytes(inputAudioFormat)
        return inputAudioFormat
    }

    fun setDelayMs(delayMs: Long) {
        val clamped = delayMs.coerceIn(-10_000L, 10_000L)
        synchronized(this) {
            requestedDelayMs = clamped
            pendingAudioFormat?.let { recomputeDelayBytes(it) }
        }
        Log.d(TAG, "setDelayMs($delayMs) → $clamped ms (delayBytes=$delayBytes)")
    }

    fun getDelayMs(): Long = requestedDelayMs

    private fun recomputeDelayBytes(format: AudioProcessor.AudioFormat) {
        val sampleRate = format.sampleRate
        val channels = format.channelCount
        val bytesPerSample = 2 // PCM_16BIT
        val frameSize = bytesPerSample * channels
        val ms = requestedDelayMs
        delayBytes = (ms * sampleRate * frameSize) / 1000L
        if (ms >= 0L) {
            bytesToPad = delayBytes
            bytesToSkip = 0L
        } else {
            bytesToPad = 0L
            bytesToSkip = -delayBytes
        }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        synchronized(this) {
            val copy = ByteBuffer.allocateDirect(inputBuffer.remaining()).order(ByteOrder.nativeOrder())
            copy.put(inputBuffer.duplicate()).flip()
            inputBuffer.position(inputBuffer.limit())
            inputBuffers.add(copy)
            drainOutput()
        }
    }

    private fun drainOutput() {
        if (currentOutput.hasRemaining()) return
        if (inputBuffers.isEmpty()) {
            currentOutput = EMPTY_BUFFER
            return
        }
        val totalBytes = inputBuffers.sumOf { it.remaining() }
        val merged = ByteBuffer.allocateDirect(totalBytes).order(ByteOrder.nativeOrder())
        while (inputBuffers.isNotEmpty()) merged.put(inputBuffers.removeFirst())
        merged.flip()

        currentOutput = when {
            bytesToPad > 0L -> {
                val padNow = minOf(bytesToPad, merged.remaining().toLong()).toInt()
                bytesToPad -= padNow.toLong()
                merged.position(merged.position() + padNow)
                merged
            }
            bytesToSkip > 0L -> {
                val skipNow = minOf(bytesToSkip, merged.remaining().toLong()).toInt()
                bytesToSkip -= skipNow.toLong()
                merged.position(merged.position() + skipNow)
                merged
            }
            else -> merged
        }
    }

    override fun getOutput(): ByteBuffer {
        synchronized(this) {
            if (!currentOutput.hasRemaining()) drainOutput()
            return currentOutput
        }
    }

    override fun isActive(): Boolean = pendingAudioFormat != null

    override fun onFlush() {
        synchronized(this) {
            inputBuffers.clear()
            currentOutput = EMPTY_BUFFER
            pendingAudioFormat?.let { recomputeDelayBytes(it) }
        }
    }

    override fun onReset() {
        synchronized(this) {
            requestedDelayMs = 0L
            delayBytes = 0L
            bytesToSkip = 0L
            bytesToPad = 0L
            inputBuffers.clear()
            currentOutput = EMPTY_BUFFER
            pendingAudioFormat = null
        }
    }
}
