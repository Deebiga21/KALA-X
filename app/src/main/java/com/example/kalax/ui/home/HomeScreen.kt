package com.example.kalax.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.kalax.ui.product.ProductViewModel
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.vector.ImageVector

val CreamBackground = Color(0xFFF9F7F3)
val SageGreen = Color(0xFFE3E9DD)
val SageGreenDark = Color(0xFFC7D3BC)
val TextDark = Color(0xFF1E1C1A)
val CardWhite = Color(0xFFFFFFFF)
val BeigeNav = Color(0xFFF3EFE9)
val BeigeNavActive = Color(0xFFE4DFD5)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCreateProductClick: () -> Unit,
    viewModel: ProductViewModel
) {
    val scrollState = rememberScrollState()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CreamBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = SageGreen,
                radius = size.width * 0.7f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.2f, size.height * 0.1f)
            )
            drawCircle(
                color = SageGreen.copy(alpha = 0.5f),
                radius = size.width * 0.5f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * 0.6f)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.Transparent, CircleShape)
                            .border(1.dp, TextDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Person, contentDescription = "Profile", tint = TextDark, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Welcome back,", color = TextDark, fontSize = 14.sp)
                        Text("Artisan", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(SageGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextDark)
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopEnd)
                            .background(Color(0xFFE57373), CircleShape)
                            .border(2.dp, CreamBackground, CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Turn your\ncraft into\ncommerce",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp,
                    lineHeight = 52.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "AI-powered support for marginalized artisans to create, price and sell their handmade products.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = TextDark.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Staggered Cards Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
            ) {
                // 1. SMART CATALOG
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(x = (-10).dp, y = 0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.zIndex(1f).offset(x = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.GridView, contentDescription = null, tint = TextDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SMART CATALOG", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1610701596007-11502861dcfa?auto=format&fit=crop&w=300&q=80",
                            contentDescription = "Shelf",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(140.dp)
                                .height(90.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }

                // 2. SMART PRICE
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(horizontal = 32.dp)
                        .offset(y = 10.dp)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=500&q=80",
                        contentDescription = "Ledger",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .padding(top = 24.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                    
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.align(Alignment.TopStart).offset(x = 24.dp, y = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("SMART PRICE", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
                
                // 3. MARKET READY
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(x = (-30).dp, y = 0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.zIndex(1f).offset(x = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Home, contentDescription = null, tint = TextDark, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("MARKET READY", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1533900298318-6b8da08a523e?auto=format&fit=crop&w=300&q=80",
                            contentDescription = "Market",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onCreateProductClick() }
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(SageGreen, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.GridView, contentDescription = null, tint = TextDark, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CREATE NEW LISTING:", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Capture photos, add details, and let AI do the work.", color = TextDark.copy(alpha = 0.7f), fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
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
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(BeigeNav)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                icon = Icons.Filled.Home, 
                label = "Home", 
                selected = currentRoute == "Home", 
                onClick = { onNavigate("Home") }
            )
            BottomNavItem(
                icon = Icons.Outlined.GridView, 
                label = "Catalog", 
                selected = currentRoute == "Catalog", 
                onClick = { onNavigate("Catalog") }
            )
            BottomNavItem(
                icon = Icons.Outlined.BarChart, 
                label = "Insights", 
                selected = currentRoute == "Insights", 
                onClick = { onNavigate("Insights") }
            )
            BottomNavItem(
                icon = Icons.Outlined.Person, 
                label = "Profile", 
                selected = currentRoute == "Profile", 
                onClick = { onNavigate("Profile") }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector, 
    label: String, 
    selected: Boolean, 
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }.padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (selected) BeigeNavActive else Color.Transparent, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon, 
                contentDescription = label, 
                tint = TextDark,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = TextDark, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}
