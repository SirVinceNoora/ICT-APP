package com.example.ictapp.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ictapp.R
import kotlinx.coroutines.delay

@Composable
fun ByteForgeSplashScreen(onAnimationFinished: () -> Unit) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "alpha"
    )
    
    val scaleAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    // Secondary animation for the "Forged by" text
    var showSignature by remember { mutableStateOf(false) }
    val signatureAlpha = animateFloatAsState(
        targetValue = if (showSignature) 0.6f else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 500),
        label = "signatureAlpha"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(300)
        showSignature = true
        delay(2500) // Display for 2.8 seconds total
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF000000)), // Solid black background for the logo
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.byteforge),
                contentDescription = "ByteForge Logo",
                modifier = Modifier
                    .size(280.dp)
                    .alpha(alphaAnim.value)
                    .scale(scaleAnim.value)
            )
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                text = "FORGED BY V1NC3",
                color = Color.Cyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                modifier = Modifier
                    .alpha(signatureAlpha.value)
                    .offset(y = (20 * (1f - signatureAlpha.value)).dp) // Subtle slide up
            )
        }
    }
}
