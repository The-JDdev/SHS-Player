package dev.anilbeesetti.nextplayer.feature.player.extensions

import android.util.Rational

/**
 * Coerce a Rational to the Picture-in-Picture safe aspect ratio range.
 *
 * Android requires PiP aspect ratio to be between 1:2.39 (max tall) and 2.39:1 (max wide).
 * Values outside this range cause `enterPictureInPictureMode` to throw
 * `IllegalArgumentException` on some devices — particularly 32-bit ARMv7 devices
 * with older framework PiP implementations, where the bounds check fails differently.
 *
 * This helper normalises the ratio into the safe window so PiP entry cannot fail
 * regardless of the source video's dimensions.
 */
fun Rational.coercePiPSafe(): Rational {
    val min = 1f / 2.39f
    val max = 2.39f
    val current = toFloat()
    return when {
        current < min -> Rational(100, 239)
        current > max -> Rational(239, 100)
        else -> this
    }
}

/**
 * Alias for backward compatibility with the PlayerActivity.kt call site.
 */
fun Rational.coerce(): Rational = coercePiPSafe()
