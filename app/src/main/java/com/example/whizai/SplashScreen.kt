package com.example.whizai

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val logoScale = remember { Animatable(0f) }
    val imageOffsetY = remember { Animatable(300f) } // Start image off-screen at the bottom
    val imageAlpha = remember { Animatable(0f) }

    LaunchedEffect(key1 =  true) {
        logoScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(2f).getInterpolation(it) }
            )
        )
        imageAlpha.animateTo( targetValue = 1f, animationSpec = tween(durationMillis = 900))
        imageOffsetY.animateTo(targetValue = 0f, animationSpec = tween(durationMillis = 900))

        delay(2500L)
        navController.navigate("login_screen")
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "WhizAI Logo",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
                .scale(logoScale.value) // Apply the scale animation
                .size(150.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.intro_screen_pic_one),
            contentDescription = "Welcome Illustration",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .offset(y = imageOffsetY.value.dp) // Apply the slide animation
                .alpha(imageAlpha.value) // Apply the fade-in animation
        )
    }
}