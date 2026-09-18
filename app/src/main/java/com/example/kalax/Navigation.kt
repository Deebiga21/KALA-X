package com.example.kalax

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.kalax.ui.main.MainScreen
import com.example.kalax.ui.main.CatalogScreen
import com.example.kalax.ui.main.InsightsScreen
import com.example.kalax.ui.main.ProfileScreen
import com.example.kalax.ui.main.ProductDetailScreen
import com.example.kalax.ui.home.HomeScreen
import com.example.kalax.ui.product.CaptureScreen
import com.example.kalax.ui.product.*
import com.example.kalax.ui.onboarding.SplashScreen
import com.example.kalax.ui.onboarding.OnboardingScreen
import com.example.kalax.ui.onboarding.LoginScreen
import kotlinx.serialization.Serializable

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(Splash)
  val context = LocalContext.current
  val app = context.applicationContext as KalaXApplication
  val productViewModel: ProductViewModel = viewModel(factory = ProductViewModel.provideFactory(app))

  val onNavigate: (String) -> Unit = { route ->
    if (route.startsWith("ProductDetail/")) {
        val id = route.removePrefix("ProductDetail/")
        backStack.add(ProductDetail(id))
    } else if (route == "CreateProduct") {
        backStack.add(Capture)
    } else {
        backStack.clear()
        when (route) {
          "Home" -> backStack.add(Home)
          "Catalog" -> backStack.add(Catalog)
          "Insights" -> backStack.add(Insights)
          "Profile" -> backStack.add(Profile)
        }
    }
  }

  if (backStack.isEmpty()) {
      LaunchedEffect(Unit) {
          backStack.add(Splash)
      }
      return // Wait for LaunchedEffect to seed the backstack
  }

  val activity = context as? android.app.Activity ?: (context as? android.content.ContextWrapper)?.baseContext as? android.app.Activity

  NavDisplay(
    backStack = backStack,
    onBack = { 
        if (backStack.size <= 1) {
            activity?.finish()
        } else {
            backStack.removeLastOrNull()
        }
    },
    entryProvider =
      entryProvider {
        entry<Splash> {
          SplashScreen(
              onNavigateToOnboarding = {
                  backStack.clear()
                  backStack.add(Onboarding)
              },
              onNavigateToHome = {
                  backStack.clear()
                  backStack.add(Home)
              },
              onNavigateToLogin = {
                  backStack.clear()
                  backStack.add(Login)
              }
          )
        }
        entry<Onboarding> {
          OnboardingScreen(
              onFinish = {
                  backStack.clear()
                  backStack.add(Login)
              }
          )
        }
        entry<Login> {
          LoginScreen(
              viewModel = productViewModel,
              onLoginSuccess = {
                  backStack.clear()
                  backStack.add(Home)
              }
          )
        }
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
        entry<ProductDetail> { args ->
            ProductDetailScreen(
                productId = args.productId,
                viewModel = productViewModel,
                onBack = { backStack.removeLastOrNull() }
            )
        }
      },
  )
}
