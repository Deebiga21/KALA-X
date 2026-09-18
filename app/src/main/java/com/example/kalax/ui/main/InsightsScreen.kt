package com.example.kalax.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalax.ui.home.BottomNavBar
import com.example.kalax.ui.product.ProductViewModel
import androidx.compose.ui.zIndex
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.drawscope.Stroke

val InsightsBg = Color(0xFFEAF0E2) // Very light green bg
val CardGreen = Color(0xFFC7DAB3) // Darker sage card
val CardGreenLight = Color(0xFFD6E5C3)
val TextDarkGreen = Color(0xFF2A3A1F)
val TealAccent = Color(0xFF0F9D58)

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProductViewModel
) {
    val insights by viewModel.marketInsights.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchInsights()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(InsightsBg)
    ) {
        // Subtle background shapes if desired
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = CardGreen.copy(alpha = 0.5f),
                radius = size.width * 0.5f,
                center = androidx.compose.ui.geometry.Offset(size.width * 0.8f, size.height * -0.1f)
            )
            drawCircle(
                color = CardGreen.copy(alpha = 0.5f),
                radius = size.width * 0.4f,
                center = androidx.compose.ui.geometry.Offset(size.width * 1.1f, size.height * 0.3f)
            )
        }

        if (insights == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextDarkGreen)
            }
        } else {
            val data = insights!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 40.dp, bottom = 120.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.TrendingUp, contentDescription = null, tint = TextDarkGreen, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Market Insights",
                            color = TextDarkGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Know what buyers want. Price with confidence.",
                        color = TextDarkGreen.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Main Pricing Card
                item {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CardGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Recommended Price", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 12.sp)
                                    Text("₹${data.recommended_price}", color = TextDarkGreen, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Your Cost", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 12.sp)
                                    Text("₹${data.your_cost}", color = TextDarkGreen, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Potential Margin", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 12.sp)
                                    Text("₹${data.potential_margin}", color = TextDarkGreen, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(32.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        progress = { data.confidence_score / 100f },
                                        modifier = Modifier.size(60.dp),
                                        color = TextDarkGreen,
                                        trackColor = TextDarkGreen.copy(alpha = 0.2f),
                                        strokeWidth = 6.dp
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(data.opportunity_level, color = TextDarkGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("${data.confidence_score}% confidence", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 14.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Demand & Trends
                item {
                    Text("Demand & Trends", color = TextDarkGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CardGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(data.top_category, color = TextDarkGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("Demand", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(data.demand_level, color = TealAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Column {
                                    Text("Trend", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("↑ ${data.trend_percentage}%", color = TextDarkGreen.copy(alpha=0.5f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                                Column {
                                    Text("Buyer Interest", color = TextDarkGreen.copy(alpha=0.7f), fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("+${data.buyer_interest_percentage}%", color = TextDarkGreen.copy(alpha=0.5f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Opportunity Description Card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = CardGreenLight),
                        border = BorderStroke(1.dp, CardGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(24.dp).background(TextDarkGreen.copy(alpha=0.1f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = TextDarkGreen, modifier = Modifier.size(16.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Best Opportunity", color = TextDarkGreen, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(data.top_category, color = TextDarkGreen.copy(alpha=0.5f), fontSize = 14.sp)
                                    }
                                }
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextDarkGreen)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = data.opportunity_description,
                                color = TextDarkGreen.copy(alpha=0.8f),
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(50),
                                border = BorderStroke(1.dp, TextDarkGreen.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDarkGreen)
                            ) {
                                Text("View Opportunity", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
        
        // Use the existing shared BottomNavBar from HomeScreen or define a local one
        Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp)) {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EFE9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    com.example.kalax.ui.home.BottomNavItem(icon = Icons.Outlined.Home, label = "Home", selected = currentRoute == "Home", onClick = { onNavigate("Home") })
                    com.example.kalax.ui.home.BottomNavItem(icon = Icons.Outlined.GridView, label = "Catalog", selected = currentRoute == "Catalog", onClick = { onNavigate("Catalog") })
                    com.example.kalax.ui.home.BottomNavItem(icon = Icons.Outlined.BarChart, label = "Insights", selected = currentRoute == "Insights", onClick = { onNavigate("Insights") })
                    com.example.kalax.ui.home.BottomNavItem(icon = Icons.Outlined.Person, label = "Profile", selected = currentRoute == "Profile", onClick = { onNavigate("Profile") })
                }
            }
        }
    }
}
