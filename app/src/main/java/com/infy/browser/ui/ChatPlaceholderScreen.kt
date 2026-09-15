package com.infy.browser.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.infy.browser.R
import com.infy.browser.ui.components.StarFieldBackground
import com.infy.browser.ui.theme.InfyNeonBlue
import com.infy.browser.ui.theme.InfySurfaceAlt
import com.infy.browser.ui.theme.InfyTextPrimary
import com.infy.browser.ui.theme.InfyTextSecondary

/**
 * Placeholder for the INFY AI chat surface. Intentionally not wired to any
 * network call yet — this is where a future AI API integration would plug
 * in a real conversation UI.
 */
@Composable
fun ChatPlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        StarFieldBackground(modifier = Modifier.fillMaxSize(), starCount = 40)

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(InfySurfaceAlt),
                contentAlignment = Alignment.Center
            ) {
                Text("∞", color = InfyNeonBlue, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.chat_placeholder_title),
                color = InfyTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.chat_placeholder_body),
                color = InfyTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
