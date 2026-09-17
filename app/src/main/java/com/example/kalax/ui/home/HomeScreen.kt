package com.example.kalax.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontStyle

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCreateProductClick: () -> Unit,
    viewModel: com.example.kalax.ui.product.ProductViewModel
) {
    val catalog by viewModel.catalog.collectAsState()
    val publishedCount = catalog.count { it.status == "Published" }
    val totalCount = catalog.size
    // Colors
    val navyBg = Color(0xFF020617)
    val cardBg = Color(0xFF0F172A).copy(alpha = 0.6f)
    val cyanGlow = Color(0xFF06B6D4)
    val blueGlow = Color(0xFF3B82F6)
    val violetGlow = Color(0xFF8B5CF6)
    
    // Background gradient animation
    val infiniteTransition = rememberInfiniteTransition(label = "bg_anim")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bg_offset"
    )

    val context = androidx.compose.ui.platform.LocalContext.current
    val prefs = context.getSharedPreferences("kalax_prefs", android.content.Context.MODE_PRIVATE)
    val artisanName = prefs.getString("artisan_name", "Artisan") ?: "Artisan"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(navyBg)
    ) {
        // Animated background blobs
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-50).dp, y = (-50 + animOffset).dp)
                .background(blueGlow.copy(alpha = 0.15f), CircleShape)
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 50.dp, y = (100 - animOffset).dp)
                .background(cyanGlow.copy(alpha = 0.1f), CircleShape)
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.BottomStart)
                .offset(x = 20.dp, y = (50 + animOffset).dp)
                .background(violetGlow.copy(alpha = 0.1f), CircleShape)
                .blur(80.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp) // padding for bottom nav
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E293B), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = cyanGlow, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Welcome back,", color = Color.Gray, fontSize = 12.sp)
                        Text(artisanName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White, modifier = Modifier.size(20.dp))
                    // Notification dot
                    Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(6.dp).background(Color.Red, CircleShape))
                }
            }

            // Hero Section
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                val heroText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("Turn your\n")
                    }
                    withStyle(style = SpanStyle(
                        brush = Brush.linearGradient(listOf(cyanGlow, blueGlow))
                    )) {
                        append("craft ")
                    }
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("into\n")
                    }
                    withStyle(style = SpanStyle(
                        brush = Brush.linearGradient(listOf(cyanGlow, blueGlow))
                    )) {
                        append("commerce.")
                    }
                }
                
                Text(
                    text = heroText,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 48.sp,
                    letterSpacing = (-1).sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "AI-powered support for marginalized artisans to create, price and sell their handmade products.",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // AI Product Studio WebView
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF0F172A).copy(0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                ) {
                    androidx.compose.ui.viewinterop.AndroidView(
                        factory = { context ->
                            android.webkit.WebView(context).apply {
                                settings.javaScriptEnabled = true
                                settings.allowFileAccess = true
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                loadUrl("file:///android_asset/liquid_glass.html")
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actions
            Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                
                // Primary Action
                // Primary Action
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.createDraft("New Product", "Unknown", "Unknown")
                        onCreateProductClick()
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, Brush.linearGradient(listOf(blueGlow.copy(0.5f), cyanGlow.copy(0.5f))))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(blueGlow.copy(0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = blueGlow)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Create Product", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Capture, describe and let AI do the rest.", color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.LightGray)
                    }
                }

                // Secondary Action 1
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigate("Catalog") },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, Color.White.copy(0.05f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(cyanGlow.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Storefront, contentDescription = null, tint = cyanGlow)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("My Catalog", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Manage your products & listings.", color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.LightGray)
                    }
                }

                // Secondary Action 2
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onNavigate("Insights") },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = BorderStroke(1.dp, Color.White.copy(0.05f))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(violetGlow.copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.TrendingUp, contentDescription = null, tint = violetGlow)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Market Insights", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Trends, demand & best pricing.", color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.LightGray)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Stats Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = BorderStroke(1.dp, Color.White.copy(0.05f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(Icons.Outlined.Inventory2, blueGlow, "${totalCount}", "Products Created")
                    Box(modifier = Modifier.width(1.dp).height(50.dp).background(Color.White.copy(0.1f)))
                    StatItem(Icons.Outlined.CheckCircle, cyanGlow, "${publishedCount}", "Published")
                    Box(modifier = Modifier.width(1.dp).height(50.dp).background(Color.White.copy(0.1f)))
                    StatItem(Icons.Outlined.StarOutline, violetGlow, "91/100", "Commerce Score")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Recent Activity
            Text("Recent Activity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (catalog.isNotEmpty()) {
                        Text("“${catalog.last().name} published”", color = Color.White, fontSize = 14.sp)
                    } else {
                        Text("“No recent activity.”", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Bottom Illustration Text
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Supporting Artisans.", color = cyanGlow.copy(0.8f), fontStyle = FontStyle.Italic, fontSize = 12.sp)
                Text("Building a Better Tomorrow.", color = Color.White, fontWeight = FontWeight.Medium, fontSize = 12.sp)
            }
        }
        
        BottomNavBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            currentRoute = currentRoute,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val cardBg = Color(0xFF0F172A).copy(alpha = 0.9f)
    val cyanGlow = Color(0xFF06B6D4)

    Box(modifier = modifier) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = cardBg),
            border = BorderStroke(1.dp, Color.White.copy(0.1f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Filled.Home, 
                    label = "Home", 
                    selected = currentRoute == "Home", 
                    activeColor = cyanGlow,
                    onClick = { onNavigate("Home") }
                )
                BottomNavItem(
                    icon = Icons.Outlined.GridView, 
                    label = "Catalog", 
                    selected = currentRoute == "Catalog", 
                    activeColor = cyanGlow,
                    onClick = { onNavigate("Catalog") }
                )
                BottomNavItem(
                    icon = Icons.Outlined.BarChart, 
                    label = "Insights", 
                    selected = currentRoute == "Insights", 
                    activeColor = cyanGlow,
                    onClick = { onNavigate("Insights") }
                )
                BottomNavItem(
                    icon = Icons.Outlined.Person, 
                    label = "Profile", 
                    selected = currentRoute == "Profile", 
                    activeColor = cyanGlow,
                    onClick = { onNavigate("Profile") }
                )
            }
        }
    }
}

@Composable
fun StatItem(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(32.dp).border(1.dp, color.copy(0.3f), CircleShape), 
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    label: String, 
    selected: Boolean, 
    activeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(4.dp)
    ) {
        Icon(
            icon, 
            contentDescription = label, 
            tint = if (selected) activeColor else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(modifier = Modifier.size(4.dp).background(activeColor, CircleShape))
        }
    }
}
