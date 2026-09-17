package com.example.kalax.ui.onboarding

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("kalax_prefs", Context.MODE_PRIVATE)
        val onboarded = prefs.getBoolean("has_onboarded", false)
        val loggedIn = prefs.getBoolean("is_logged_in", false)
        
        delay(1000) // minimum logo display time
        
        if (!onboarded) {
            onNavigateToOnboarding()
        } else if (!loggedIn) {
            onNavigateToLogin()
        } else {
            onNavigateToHome()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Storefront,
            contentDescription = "Logo",
            tint = Color(0xFF06B6D4),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "KALA-X",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "From Handmade to Market-Ready",
            color = Color.Gray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(48.dp))
        CircularProgressIndicator(
            color = Color(0xFF06B6D4),
            modifier = Modifier.size(24.dp),
            strokeWidth = 2.dp
        )
    }
}
