package com.example.kalax.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun CatalogScreen(
    modifier: Modifier = Modifier,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    viewModel: ProductViewModel
) {
    val catalog by viewModel.catalog.collectAsState()
    val published = catalog.count { it.status == "Published" }
    val drafts = catalog.count { it.status == "Draft" }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF020617))) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("My Catalog", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your digital shelf of handmade products.", color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatCard("${catalog.size}", "Products")
                StatCard("$published", "Published")
                StatCard("$drafts", "Drafts")
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            if (catalog.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Catalog is empty.", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(catalog) { product ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A).copy(0.6f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Box(modifier = Modifier.size(80.dp).background(Color.LightGray, RoundedCornerShape(8.dp)))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("₹${product.recommendedPrice}", color = Color(0xFF06B6D4), fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(product.category, color = Color.Gray, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${product.score}/100", color = Color(0xFF8B5CF6), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        val statusColor = if (product.status == "Published") Color(0xFF10B981) else Color(0xFFF59E0B)
                                        Text("● ${product.status}", color = statusColor, fontSize = 12.sp)
                                    }
                                }
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
fun StatCard(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, color = Color.Gray, fontSize = 12.sp)
    }
}
