package com.example.kalax

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.kalax.ui.main.MainScreen
import com.example.kalax.ui.main.CatalogScreen
import com.example.kalax.ui.main.InsightsScreen
import com.example.kalax.ui.main.ProfileScreen
import com.example.kalax.ui.home.HomeScreen
import com.example.kalax.ui.product.CaptureScreen
import com.example.kalax.ui.product.*

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Home)
  val productViewModel: ProductViewModel = viewModel()

  val onNavigate: (String) -> Unit = { route ->
    backStack.clear()
    when (route) {
      "Home" -> backStack.add(Home)
      "Catalog" -> backStack.add(Catalog)
      "Insights" -> backStack.add(Insights)
      "Profile" -> backStack.add(Profile)
    }
  }

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<Home> {
          HomeScreen(
              modifier = Modifier.safeDrawingPadding(),
              currentRoute = "Home",
              onNavigate = onNavigate,
              onCreateProductClick = { backStack.add(Capture) },
              viewModel = productViewModel
          )
        }
        entry<Catalog> {
          CatalogScreen(
              modifier = Modifier.safeDrawingPadding(),
              currentRoute = "Catalog",
              onNavigate = onNavigate,
              viewModel = productViewModel
          )
        }
        entry<Insights> {
          InsightsScreen(
              modifier = Modifier.safeDrawingPadding(),
              currentRoute = "Insights",
              onNavigate = onNavigate,
              viewModel = productViewModel
          )
        }
        entry<Profile> {
          ProfileScreen(
              modifier = Modifier.safeDrawingPadding(),
              currentRoute = "Profile",
              onNavigate = onNavigate,
              viewModel = productViewModel
          )
        }
        entry<Main> {
          MainScreen(modifier = Modifier.safeDrawingPadding().padding(16.dp))
        }
        // Product Creation Flow
        entry<Capture> {
          CaptureScreen(
              viewModel = productViewModel,
              onBack = { backStack.removeLastOrNull() },
              onNext = { backStack.add(Enhance) }
          )
        }
        entry<Enhance> {
          EnhanceScreen(
              viewModel = productViewModel,
              onBack = { backStack.removeLastOrNull() },
              onNext = { backStack.add(VoiceCatalog) }
          )
        }
        entry<VoiceCatalog> {
          VoiceCatalogScreen(
              viewModel = productViewModel,
              onBack = { backStack.removeLastOrNull() },
              onNext = { backStack.add(Pricing) }
          )
        }
        entry<Pricing> {
          PricingScreen(
              viewModel = productViewModel,
              onBack = { backStack.removeLastOrNull() },
              onNext = { backStack.add(Readiness) }
          )
        }
        entry<Readiness> {
          ReadinessScreen(
              viewModel = productViewModel,
              onBack = { backStack.removeLastOrNull() },
              onNext = { backStack.add(FinalListing) }
          )
        }
        entry<FinalListing> {
          FinalListingScreen(
              viewModel = productViewModel,
              onBack = { backStack.removeLastOrNull() },
              onPublish = {
                  productViewModel.publishDraft()
                  backStack.clear()
                  backStack.add(Catalog)
              }
          )
        }
      },
  )
}
