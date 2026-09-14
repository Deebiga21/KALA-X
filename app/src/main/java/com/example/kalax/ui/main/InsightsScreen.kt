package com.example.kalax.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProductViewModel
) {
    val catalog by viewModel.catalog.collectAsState()
    
    Box(modifier = modifier.fillMaxSize().background(Color(0xFF020617))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Market Insights", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Know what buyers want. Price with confidence.", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                item {
                    Text("TRENDING PRODUCTS", color = Color(0xFF06B6D4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TrendCard("Bamboo Crafts", "↑ 18%")
                        TrendCard("Handwoven Bags", "↑ 12%")
                        TrendCard("Terracotta", "↑ 9%")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                if (catalog.isNotEmpty()) {
                    item {
                        val product = catalog.last()
                        Text("PRICE INTELLIGENCE", color = Color(0xFF06B6D4), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(0.6f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("Current Market Range", color = Color.Gray, fontSize = 12.sp)
                                        Text("₹${product.recommendedPrice - 50} — ₹${product.recommendedPrice + 50}", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Recommended", color = Color.Gray, fontSize = 12.sp)
                                        Text("₹${product.recommendedPrice}", color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Consider pricing your ${product.name} around ₹${product.recommendedPrice}.", color = Color.LightGray, fontSize = 14.sp)
                            }
                        }
                    }
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
fun TrendCard(title: String, trend: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(0.6f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(trend, color = Color(0xFF10B981), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
