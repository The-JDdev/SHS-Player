package dev.anilbeesetti.nextplayer.feature.player.state

import android.app.Activity
import android.util.Log
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.effect.RgbAdjustment

/**
 * Video equalizer state — drives contrast & saturation in real-time on top of the
 * ExoPlayer video output via Media3's RgbAdjustment effect (Media3 1.9+).
 *
 * Brightness is handled separately by [BrightnessState] (controls window
 * screenBrightness, not the video pixels) — this is the standard Android pattern
 * (also used by mpv-android for screen brightness, see TouchGestures.kt).
 *
 * Ported from mpv-android concepts (MPVActivity.kt:1523):
 *   mpv "contrast"     → RgbAdjustment.contrastAdjustment   (range -100..+100, 0=neutral)
 *   mpv "saturation"   → RgbAdjustment.saturationAdjustment (range -100..+100, 0=neutral)
 *   mpv "brightness"   → screen brightness via WindowManager (range -100..+100, 0=neutral)
 *
 * Internal storage uses mpv-style -100..+100 (matches reference repo exactly).
 * UI contract: the existing EqualizerView passes values in 0..2 range (1f = neutral),
 * which we translate to/from mpv-style via [_multiplierToMpv] / [_mpvToMultiplier].
 */
@UnstableApi
@Composable
fun rememberVideoEqualizerState(player: Player?, activity: Activity?): VideoEqualizerState {
    val state = remember { VideoEqualizerState() }
    LaunchedEffect(player) { state.bindPlayer(player as? ExoPlayer) }
    LaunchedEffect(activity) { state.bindActivity(activity) }
    DisposableEffect(Unit) { onDispose { state.unbindAll() } }
    return state
}

@UnstableApi
@Stable
class VideoEqualizerState {
    /** mpv-style range: -100..+100, 0 = neutral */
    var brightnessMpv by mutableFloatStateOf(0f)
        private set
    var contrastMpv by mutableFloatStateOf(0f)
        private set
    var saturationMpv by mutableFloatStateOf(0f)
        private set

    /** UI-facing 0..2 multipliers (1f = neutral) — exposed for the existing EqualizerView UI */
    val brightnessMultiplier: Float get() = _mpvToMultiplier(brightnessMpv)
    val contrastMultiplier: Float get() = _mpvToMultiplier(contrastMpv)
    val saturationMultiplier: Float get() = _mpvToMultiplier(saturationMpv)

    private var exoPlayer: ExoPlayer? = null
    private var activity: Activity? = null

    fun bindPlayer(player: ExoPlayer?) {
        exoPlayer = player
        applyVideoEffects()
    }

    fun bindActivity(a: Activity?) {
        activity = a
        applyScreenBrightness()
    }

    fun unbindAll() {
        exoPlayer = null
        activity = null
    }

    /** Accept mpv-style -100..+100 values from a future settings page */
    fun setBrightnessMpv(v: Float) {
        brightnessMpv = v.coerceIn(-100f, 100f); applyScreenBrightness()
    }
    fun setContrastMpv(v: Float) {
        contrastMpv = v.coerceIn(-100f, 100f); applyVideoEffects()
    }
    fun setSaturationMpv(v: Float) {
        saturationMpv = v.coerceIn(-100f, 100f); applyVideoEffects()
    }

    /** Accept UI slider in 0..2 range (existing EqualizerView contract) */
    fun setBrightnessFromMultiplier(m: Float) = setBrightnessMpv(_multiplierToMpv(m))
    fun setContrastFromMultiplier(m: Float) = setContrastMpv(_multiplierToMpv(m))
    fun setSaturationFromMultiplier(m: Float) = setSaturationMpv(_multiplierToMpv(m))

    fun reset() {
        brightnessMpv = 0f; contrastMpv = 0f; saturationMpv = 0f
        applyScreenBrightness(); applyVideoEffects()
    }

    fun applyProfile(b: Float, c: Float, s: Float) {
        // Profile values come in as 0..2 multipliers — convert to mpv-style -100..100
        brightnessMpv = _multiplierToMpv(b)
        contrastMpv = _multiplierToMpv(c)
        saturationMpv = _multiplierToMpv(s)
        applyScreenBrightness(); applyVideoEffects()
    }

    @OptIn(UnstableApi::class)
    private fun applyVideoEffects() {
        val ep = exoPlayer ?: return
        try {
            // Contrast & saturation: 1f neutral (matches mpv's behaviour when its -100..100 is 0)
            val rgb = RgbAdjustment.Builder()
                .setContrastAdjustment(contrastMultiplier.coerceIn(0f, 2f))
                .setSaturationAdjustment(saturationMultiplier.coerceIn(0f, 2f))
                .build()
            ep.setVideoEffects(listOf(rgb))
        } catch (e: Exception) {
            Log.w("VideoEq", "applyVideoEffects failed", e)
        }
    }

    /**
     * Apply screen brightness using Android's WindowManager.LayoutParams.screenBrightness.
     * Mirrors mpv-android TouchGestures.kt:96 which sets screen brightness (not video brightness).
     *
     * mpv -100 → screen brightness 0.0 (dim)
     * mpv    0 → screen brightness -1 (system default, BRIGHTNESS_OVERRIDE_NONE)
     * mpv +100 → screen brightness 1.0 (max)
     */
    private fun applyScreenBrightness() {
        val a = activity ?: return
        try {
            val attrs = a.window.attributes
            attrs.screenBrightness = if (brightnessMpv == 0f) {
                WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
            } else {
                (0.5f + brightnessMpv / 200f).coerceIn(0f, 1f)
            }
            a.window.attributes = attrs
        } catch (e: Exception) {
            Log.w("VideoEq", "applyScreenBrightness failed", e)
        }
    }

    /** Convert mpv-style -100..+100 to multiplier 0..2 (1 = neutral) */
    private fun _mpvToMultiplier(mpv: Float): Float = (1f + mpv / 100f).coerceIn(0f, 2f)

    /** Convert multiplier 0..2 to mpv-style -100..+100 (0 = neutral) */
    private fun _multiplierToMpv(m: Float): Float = ((m - 1f) * 100f).coerceIn(-100f, 100f)
}
