package com.example.mediscanmain.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : NavigationItem("home", Icons.Filled.Home, "Home")
    object LogVitals : NavigationItem("log_vitals", Icons.Filled.Favorite, "Log Vitals")
    object HealthTips : NavigationItem("health_tips", Icons.Filled.Lightbulb, "Health Tips")
    object Profile : NavigationItem("profile", Icons.Filled.Person, "Profile")
}

