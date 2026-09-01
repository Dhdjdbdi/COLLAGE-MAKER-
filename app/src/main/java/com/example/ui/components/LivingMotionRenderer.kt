package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AccentPrimary
import com.example.ui.theme.AccentSecondary
import com.example.ui.theme.AccentTertiary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LivingMotionRenderer(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "livingParticles")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleProgress"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Subtle ambient edge glow
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    AccentPrimary.copy(alpha = 0.12f * pulseGlow),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.8f
            )
        )

        // Floating ambient light dust / spark points
        val particleCount = 18
        for (i in 0 until particleCount) {
            val angle = (i.toFloat() / particleCount) * 2f * Math.PI.toFloat() + (progress * 2f * Math.PI.toFloat())
            val radiusX = w * 0.4f * ((i % 3 + 1) / 3f)
            val radiusY = h * 0.4f * ((i % 3 + 1) / 3f)

            val px = w * 0.5f + cos(angle) * radiusX
            val py = h * 0.5f + sin(angle * 1.3f) * radiusY

            val sparkColor = when (i % 3) {
                0 -> AccentPrimary.copy(alpha = 0.5f * pulseGlow)
                1 -> AccentSecondary.copy(alpha = 0.45f * pulseGlow)
                else -> AccentTertiary.copy(alpha = 0.55f * pulseGlow)
            }

            drawCircle(
                color = sparkColor,
                radius = 3.5f + (i % 3),
                center = Offset(px, py)
            )
        }
    }
}
