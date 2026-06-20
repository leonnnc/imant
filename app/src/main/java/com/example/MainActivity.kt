package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CanvasScreen
import com.example.ui.CatalogScreen
import com.example.ui.CheckoutScreen
import com.example.ui.LoginScreen
import com.example.ui.MagnetViewModel
import com.example.ui.Screen
import com.example.ui.SettingsScreen
import com.example.ui.SupportScreen
import com.example.ui.TrackingScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MagnetViewModel = viewModel()
      val isDark by viewModel.isDarkTheme.collectAsState()
      val currentScreen by viewModel.currentScreen.collectAsState()

      MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
        when (currentScreen) {
          Screen.LOGIN -> LoginScreen(viewModel)
          Screen.CATALOG -> CatalogScreen(viewModel)
          Screen.CANVAS -> CanvasScreen(viewModel)
          Screen.CHECKOUT -> CheckoutScreen(viewModel)
          Screen.TRACKING -> TrackingScreen(viewModel)
          Screen.HELP -> SupportScreen(viewModel)
          Screen.SETTINGS -> SettingsScreen(viewModel)
        }
      }
    }
  }
}
