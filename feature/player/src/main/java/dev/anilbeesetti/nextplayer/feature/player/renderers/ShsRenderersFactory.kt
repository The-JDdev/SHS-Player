package dev.anilbeesetti.nextplayer.feature.player.renderers

import android.content.Context
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import dev.anilbeesetti.nextplayer.feature.player.audio.DelayAudioProcessor

/**
 * Phase 6.2 — Renderers factory that injects [DelayAudioProcessor] into the
 * ExoPlayer audio chain. The delay value is set live from the player UI's
 * audio-sync dialog by calling [DelayAudioProcessor.setDelayMs].
 *
 * The companion [delayAudioProcessor] is a process-singleton so that the
 * MediaPlayerScreen can hold a stable reference and call `setDelayMs` on
 * slider changes without needing a handle to the renderer instance.
 */
class ShsRenderersFactory(context: Context) : DefaultRenderersFactory(context) {

    init {
        setEnableDecoderFallback(true)
        setExtensionRendererMode(EXTENSION_RENDERER_MODE_ON)
    }

    override fun buildAudioSink(
        context: Context,
        enableFloatOutput: Boolean,
        enableAudioTrackPlaybackParams: Boolean,
    ): AudioSink {
        // Inject our delay processor as the FIRST processor in the chain so
        // it sees the raw PCM before any silence-skipping / channel-mixing.
        return DefaultAudioSink.Builder(context)
            .setAudioProcessors(arrayOf(delayAudioProcessor))
            .setEnableFloatOutput(enableFloatOutput)
            .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
            .build()
    }

    companion object {
        /**
         * Singleton [DelayAudioProcessor] — the player UI mutates this directly.
         * ExoPlayer picks it up via the [ShsRenderersFactory] when the player
         * is built in [PlayerService].
         */
        @JvmStatic
        val delayAudioProcessor: DelayAudioProcessor = DelayAudioProcessor()
    }
}
