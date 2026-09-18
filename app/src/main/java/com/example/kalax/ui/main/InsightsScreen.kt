package com.example.kalax.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalax.ui.home.BottomNavBar
import com.example.kalax.ui.product.ProductViewModel

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProductViewModel
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val cyanGlow = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = cyanGlow)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Market Insights", color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text("Know what buyers want. Price with confidence.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("* Reference / Dummy Market Data", color = MaterialTheme.colorScheme.primary, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Trending Categories", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("View all", color = cyanGlow, fontSize = 12.sp, modifier = Modifier.clickable { /* Handle View all */ })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        item { TrendCategoryCard("Bamboo Crafts", "↑ 18%") }
                        item { TrendCategoryCard("Handwoven Bags", "↑ 12%") }
                        item { TrendCategoryCard("Terracotta Decor", "↑ 9%") }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Price Intelligence", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("View all", color = cyanGlow, fontSize = 12.sp, modifier = Modifier.clickable { /* Handle View all */ })
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Bamboo Basket", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text("Market Range", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("₹799 - ₹899", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Recommended Price", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("₹849", color = cyanGlow, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("Your Cost", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("₹600", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Potential Margin", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("₹249", color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(progress = 0.87f, color = cyanGlow, trackColor = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Strong Opportunity", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("87% confidence", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text("Demand & Trends", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Bamboo Home Decor", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Demand", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("HIGH", color = Color(0xFF10B981), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Trend", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("↑ 18%", color = cyanGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                                Column {
                                    Text("Buyer Interest", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                    Text("+18%", color = cyanGlow, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, cyanGlow.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = cyanGlow)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("Best Opportunity", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Bamboo Home Decor", color = cyanGlow, fontSize = 12.sp)
                                    }
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Demand is high. Similar products are selling between ₹799 and ₹899.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { /* TODO */ },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                border = BorderStroke(1.dp, cyanGlow)
                            ) {
                                Text("View Opportunity", color = cyanGlow)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp)) // Nav bar padding
                }
            }
        }
        
        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            currentRoute = currentRoute,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun TrendCategoryCard(title: String, trend: String) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(trend, color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
