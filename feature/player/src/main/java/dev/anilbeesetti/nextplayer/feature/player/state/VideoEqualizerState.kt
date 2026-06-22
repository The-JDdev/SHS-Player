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
import androidx.media3.exoplayer.ExoPlayer

/**
 * Video equalizer state — brightness, contrast, saturation.
 *
 * Ported from mpv-android concepts (MPVActivity.kt:1523):
 *   mpv "brightness" → screen brightness via WindowManager.LayoutParams.screenBrightness
 *                     (mirrors mpv-android TouchGestures.kt:96 pattern)
 *   mpv "contrast" / "saturation" → stored as state for future renderer integration.
 *
 * The Media3 RgbAdjustment effect (androidx.media3.effect.RgbAdjustment) was originally
 * used here, but the Media3 1.9.2 release does not expose it via the public `setVideoEffects`
 * API in a way compatible with our minSdk. We persist the values and apply brightness via
 * the standard Android window-screenBrightness mechanism (same as mpv-android for the
 * screen-brightness gesture).
 *
 * UI contract: the existing EqualizerView passes values in 0..2 range (1f = neutral),
 * which we translate to/from mpv-style -100..+100 internally.
 */
@Composable
fun rememberVideoEqualizerState(player: Player?, activity: Activity?): VideoEqualizerState {
    val state = remember { VideoEqualizerState() }
    LaunchedEffect(player) { state.bindPlayer(player as? ExoPlayer) }
    LaunchedEffect(activity) { state.bindActivity(activity) }
    DisposableEffect(Unit) { onDispose { state.unbindAll() } }
    return state
}

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
    }

    fun bindActivity(a: Activity?) {
        activity = a
        applyScreenBrightness()
    }

    fun unbindAll() {
        exoPlayer = null
        activity = null
    }

    /** Accept mpv-style -100..+100 values */
    fun setBrightnessMpvValue(v: Float) {
        brightnessMpv = v.coerceIn(-100f, 100f); applyScreenBrightness()
    }
    fun setContrastMpvValue(v: Float) {
        contrastMpv = v.coerceIn(-100f, 100f)
    }
    fun setSaturationMpvValue(v: Float) {
        saturationMpv = v.coerceIn(-100f, 100f)
    }

    /** Accept UI slider in 0..2 range (existing EqualizerView contract) */
    fun setBrightnessFromMultiplier(m: Float) = setBrightnessMpvValue(_multiplierToMpv(m))
    fun setContrastFromMultiplier(m: Float) = setContrastMpvValue(_multiplierToMpv(m))
    fun setSaturationFromMultiplier(m: Float) = setSaturationMpvValue(_multiplierToMpv(m))

    fun reset() {
        brightnessMpv = 0f; contrastMpv = 0f; saturationMpv = 0f
        applyScreenBrightness()
    }

    fun applyProfile(b: Float, c: Float, s: Float) {
        // Profile values come in as 0..2 multipliers — convert to mpv-style -100..100
        brightnessMpv = _multiplierToMpv(b)
        contrastMpv = _multiplierToMpv(c)
        saturationMpv = _multiplierToMpv(s)
        applyScreenBrightness()
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
