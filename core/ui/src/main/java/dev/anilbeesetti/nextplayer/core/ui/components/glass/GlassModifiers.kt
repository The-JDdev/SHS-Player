package dev.anilbeesetti.nextplayer.core.ui.components.glass

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity

/**
 * Phase 1 — Glassmorphism modifier kit.
 *
 * Modern "frosted glass" aesthetic: translucent surfaces with vibrant gradient
 * tint + subtle blur + thin border highlight. Combined with the existing
 * Material 3 color scheme so it works in both light and dark themes.
 *
 * On Android S+ we use `Modifier.blur()` which delegates to the hardware
 * RenderEffect — fast and free. On older Android we fall back to a translucent
 * gradient tint only (no blur), since software blur is too slow.
 *
 * Usage:
 *   Box(Modifier.glassCard()) { ... }
 *   Box(Modifier.glassPanel(cornerRadius = 24.dp, alpha = 0.55f)) { ... }
 */
fun Modifier.glassCard(
    cornerRadius: Dp = 20.dp,
    alpha: Float = 0.6f,
    blurRadius: Dp = 24.dp,
): Modifier = composed {
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val baseTint = if (isDark) Color(0xFF1A1F2E) else Color(0xFFFFFFFF)
    val accent = MaterialTheme.colorScheme.primary
    val onAccent = MaterialTheme.colorScheme.tertiary
    val tint = baseTint.copy(alpha = alpha)

    this
        .clip(RoundedCornerShape(cornerRadius))
        .drawBehind {
            // Layer 1 — translucent base tint (the "glass")
            drawRect(color = tint)
            // Layer 2 — vibrant gradient overlay (top-left → bottom-right)
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.18f),
                        Color.Transparent,
                        onAccent.copy(alpha = 0.14f),
                    ),
                ),
            )
            // Layer 3 — top-edge highlight (light refraction effect)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color.Transparent,
                    ),
                    startY = 0f,
                    endY = size.height * 0.25f,
                ),
            )
        }
        .let {
            // Hardware-accelerated blur on S+; no-op below (the tint layers
            // already give a frosted feel without actual blur).
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                it.blur(blurRadius)
            } else it
        }
}

/**
 * A glass panel with a thin highlighted border — used for buttons, chips,
 * and tiles inside the player overlay.
 */
fun Modifier.glassPanel(
    cornerRadius: Dp = 16.dp,
    alpha: Float = 0.45f,
): Modifier = composed {
    val accent = MaterialTheme.colorScheme.primary
    this
        .clip(RoundedCornerShape(cornerRadius))
        .drawBehind {
            drawRect(color = Color.White.copy(alpha = alpha))
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        accent.copy(alpha = 0.35f),
                        Color.Transparent,
                    ),
                ),
            )
        }
}

/**
 * Pre-built composable wrapper — a frosted-glass card with a vibrant tint.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    alpha: Float = 0.6f,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .glassCard(cornerRadius = cornerRadius, alpha = alpha)
            .padding(contentPadding),
    ) {
        content()
    }
}

/**
 * Pre-built composable wrapper — a small frosted-glass tile for icons.
 */
@Composable
fun GlassIconButton(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .glassPanel(cornerRadius = 14.dp, alpha = 0.4f)
            .clip(RoundedCornerShape(14.dp))
            .padding(8.dp),
    ) {
        content()
    }
}

private fun Color.luminance(): Float = 0.299f * red + 0.587f * green + 0.114f * blue
