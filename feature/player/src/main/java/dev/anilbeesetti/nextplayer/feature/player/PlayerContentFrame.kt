package dev.anilbeesetti.nextplayer.feature.player

import android.graphics.Rect
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_SURFACE_VIEW
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.modifiers.resizeWithContentScale
import androidx.media3.ui.compose.state.rememberPresentationState
import dev.anilbeesetti.nextplayer.feature.player.extensions.toContentScale
import dev.anilbeesetti.nextplayer.feature.player.state.ControlsVisibilityState
import dev.anilbeesetti.nextplayer.feature.player.state.PictureInPictureState
import dev.anilbeesetti.nextplayer.feature.player.state.SeekGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.TapGestureState
import dev.anilbeesetti.nextplayer.feature.player.state.VideoZoomAndContentScaleState
import dev.anilbeesetti.nextplayer.feature.player.state.VolumeAndBrightnessGestureState
import dev.anilbeesetti.nextplayer.feature.player.ui.PlayerGestures
import dev.anilbeesetti.nextplayer.feature.player.ui.ShutterView
import dev.anilbeesetti.nextplayer.feature.player.ui.SubtitleConfiguration
import dev.anilbeesetti.nextplayer.feature.player.ui.SubtitleView

/**
 * Creates a Compose ColorMatrix that combines brightness, contrast, and saturation adjustments.
 * - brightness: 0f..2f (1f = normal, 0f = black, 2f = double bright)
 * - contrast: 0f..2f (1f = normal, 0f = flat gray, 2f = high contrast)
 * - saturation: 0f..2f (1f = normal, 0f = grayscale, 2f = oversaturated)
 */
private fun createEqColorMatrix(brightness: Float, contrast: Float, saturation: Float): ColorMatrix {
    // We combine brightness, contrast, and saturation into a single 4x5 color matrix.
    // The approach: apply brightness as RGB scaling, contrast as scaling from midpoint,
    // and saturation as a luminance-preserving desaturation.

    val b = brightness
    val c = contrast
    val s = saturation

    // Luminance weights (Rec. 709)
    val lr = 0.2126f
    val lg = 0.7152f
    val lb = 0.0722f

    // Combined matrix: Brightness * Contrast * Saturation
    // Saturation matrix (applied first)
    val sr = lr * (1f - s) + s
    val sg = lg * (1f - s)
    val sb = lb * (1f - s)
    val sgr = lr * (1f - s)
    val sgg = lg * (1f - s) + s
    val sgb = lb * (1f - s)
    val sbr = lr * (1f - s)
    val sbg = lg * (1f - s)
    val sbb = lb * (1f - s) + s

    // Contrast + Brightness: each element is scaled by (c * b), offset by ((1-c)*0.5*255 + 0)*b
    // Actually, let's combine: M = B * C * S where B scales by brightness, C scales by contrast + offset
    // Result: element = b * c * sElement, offset = b * (1-c)*128 + 0

    val cb = c * b
    val offset = b * (1f - c) * 128f // midpoint for contrast

    val values = floatArrayOf(
        cb * sr, cb * sg, cb * sb, 0f, offset,
        cb * sgr, cb * sgg, cb * sgb, 0f, offset,
        cb * sbr, cb * sbg, cb * sbb, 0f, offset,
        0f, 0f, 0f, b, 0f, // alpha = brightness (keep alpha at 1 when b=1)
    )

    return ColorMatrix(values)
}

@OptIn(UnstableApi::class)
@Composable
fun PlayerContentFrame(
    modifier: Modifier = Modifier,
    player: Player,
    pictureInPictureState: PictureInPictureState,
    controlsVisibilityState: ControlsVisibilityState,
    tapGestureState: TapGestureState,
    seekGestureState: SeekGestureState,
    videoZoomAndContentScaleState: VideoZoomAndContentScaleState,
    volumeAndBrightnessGestureState: VolumeAndBrightnessGestureState,
    subtitleConfiguration: SubtitleConfiguration,
    isMirrored: Boolean = false,
    eqBrightness: Float = 1f,
    eqContrast: Float = 1f,
    eqSaturation: Float = 1f,
) {
    val presentationState = rememberPresentationState(player)
    // SurfaceView does not support graphicsLayer transforms (mirror/scaleX).
    // Use TextureView when mirror mode is active so the flip works correctly.
    // Also use TextureView when EQ is active (non-default values) so ColorFilter works.
    val eqActive = eqBrightness != 1f || eqContrast != 1f || eqSaturation != 1f
    val surfaceType = if (isMirrored || eqActive) SURFACE_TYPE_TEXTURE_VIEW else SURFACE_TYPE_SURFACE_VIEW

    // Build the ColorFilter for EQ
    val eqColorFilter = if (eqActive) {
        val matrix = createEqColorMatrix(eqBrightness, eqContrast, eqSaturation)
        ColorFilter.colorMatrix(matrix)
    } else null

    PlayerSurface(
        player = player,
        surfaceType = surfaceType,
        modifier = modifier
            .resizeWithContentScale(
                contentScale = videoZoomAndContentScaleState.videoContentScale.toContentScale(),
                sourceSizeDp = presentationState.videoSizeDp?.let { size ->
                    size.copy(
                        width = with(LocalDensity.current) { size.width.toDp().value },
                        height = with(LocalDensity.current) { size.height.toDp().value },
                    )
                },
            )
            .onGloballyPositioned {
                val bounds = it.boundsInWindow()
                val rect = Rect(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt(),
                )
                pictureInPictureState.setVideoViewRect(rect)
            }
            .graphicsLayer {
                scaleX = videoZoomAndContentScaleState.zoom * if (isMirrored) -1f else 1f
                scaleY = videoZoomAndContentScaleState.zoom
                translationX = videoZoomAndContentScaleState.offset.x
                translationY = videoZoomAndContentScaleState.offset.y
                if (eqColorFilter != null) {
                    colorFilter = eqColorFilter
                }
            },
    )

    PlayerGestures(
        controlsVisibilityState = controlsVisibilityState,
        tapGestureState = tapGestureState,
        pictureInPictureState = pictureInPictureState,
        seekGestureState = seekGestureState,
        videoZoomAndContentScaleState = videoZoomAndContentScaleState,
        volumeAndBrightnessGestureState = volumeAndBrightnessGestureState,
    )

    SubtitleView(
        player = player,
        isInPictureInPictureMode = pictureInPictureState.isInPictureInPictureMode,
        configuration = subtitleConfiguration,
    )

    if (presentationState.coverSurface) {
        ShutterView()
    }
}
