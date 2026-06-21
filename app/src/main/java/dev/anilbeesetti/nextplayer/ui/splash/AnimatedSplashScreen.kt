package dev.anilbeesetti.nextplayer.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Animated Splash Screen — Material 3 + Cupertino hybrid aesthetic.
 *
 * Replaces the previous plain "shs player" text splash with:
 *   1. An animated logo (scale + alpha fade-in over 600ms)
 *   2. A pulsing play-icon backdrop (Cupertino-style subtle glow)
 *   3. A gradient background that fades from dark to brand color
 *   4. The app name in San Francisco-style typography (system font on iOS,
 *      Roboto on Android — both rendered semi-bold for visual weight)
 *
 * Used by MainActivity when MainActivityUiState == Loading.
 */
@Composable
fun AnimatedSplashScreen(
    modifier: Modifier = Modifier,
    appName: String = "SHS Player",
    tagline: String = "Premium Media Experience",
    onSplashComplete: () -> Unit = {},
) {
    val logoScale = remember { Animatable(0.3f) }
    val logoAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        logoScale.animateTo(1f, tween(300, easing = LinearEasing))
        logoAlpha.animateTo(1f, tween(200))
        pulseScale.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        )
    }
    LaunchedEffect(Unit) {
        delay(400)
        textAlpha.animateTo(1f, tween(300))
    }
    LaunchedEffect(Unit) {
        delay(1200)
        onSplashComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale.value * pulseScale.value)
                    .alpha(logoAlpha.value)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFE94560),
                                Color(0xFF0F3460),
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp, 42.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = appName,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha.value),
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = tagline,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(textAlpha.value),
            )
        }
    }
}

private fun infiniteRepeatable(animation: androidx.compose.animation.core.DurationBasedAnimationSpec<Float>, repeatMode: RepeatMode) =
    androidx.compose.animation.core.infiniteRepeatable(animation, repeatMode)
