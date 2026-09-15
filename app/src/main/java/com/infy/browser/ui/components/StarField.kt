package com.infy.browser.ui.components

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.infy.browser.ui.theme.InfyNeonBlue
import com.infy.browser.ui.theme.InfyNeonPurple
import com.infy.browser.ui.theme.InfyStar
import kotlin.random.Random

private data class Star(val x: Float, val y: Float, val radius: Float, val alpha: Float)

/**
 * Deliberately understated: a handful of soft, dim points of light and a
 * faint rotating infinity ring, kept lightweight so it never competes with
 * the search box or costs meaningful battery/CPU.
 */
@Composable
fun StarFieldBackground(modifier: Modifier = Modifier, starCount: Int = 70) {
    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 1.6f + 0.4f,
                alpha = Random.nextFloat() * 0.5f + 0.15f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "infy_bg")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Minimal infinity symbol watermark, very low opacity.
            rotate(degrees = ringRotation * 0.02f, pivot = Offset(w / 2f, h * 0.42f)) {
                val cx = w / 2f
                val cy = h * 0.42f
                val r = w * 0.16f
                drawCircle(
                    color = InfyNeonBlue.copy(alpha = 0.05f),
                    radius = r,
                    center = Offset(cx - r, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                )
                drawCircle(
                    color = InfyNeonPurple.copy(alpha = 0.05f),
                    radius = r,
                    center = Offset(cx + r, cy),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                )
            }

            stars.forEach { star ->
                drawCircle(
                    color = InfyStar.copy(alpha = star.alpha * twinkle),
                    radius = star.radius,
                    center = Offset(star.x * w, star.y * h)
                )
            }
        }
    }
}

val InfyBackgroundColor = Color.Black
