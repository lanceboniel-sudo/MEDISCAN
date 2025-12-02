package com.example.mediscanmain

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.mediscanmain.ui.auth.LoginScreen
import com.example.mediscanmain.ui.auth.RegisterScreen
import com.example.mediscanmain.ui.home.HomeScreen
import com.example.mediscanmain.ui.logvitals.LogVitalsScreen
import com.example.mediscanmain.ui.healthtips.HealthTipsScreen
import com.example.mediscanmain.ui.profile.ProfileScreen
import com.example.mediscanmain.ui.components.BottomBar
import com.example.mediscanmain.data.ThemePreferences
import com.example.mediscanmain.data.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("FirebaseTest", "Current user: ${FirebaseAuth.getInstance().currentUser?.email}")
        super.onCreate(savedInstanceState)

        val bottomBarRoutes = listOf("home", "logvitals", "healthtips", "profile")
        val themePrefs = ThemePreferences(this)

        setContent {
            val scope = rememberCoroutineScope()
            val isDarkModeFlow = themePrefs.isDarkMode.collectAsState(initial = false)
            var isDarkMode by remember { mutableStateOf(isDarkModeFlow.value) }

            val themeColors = if (isDarkMode) darkColorScheme() else lightColorScheme()
            val navController = rememberNavController()
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            MaterialTheme(colorScheme = themeColors) {
                Scaffold(
                    bottomBar = {
                        if (currentRoute in bottomBarRoutes) {
                            BottomBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                navController = navController,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable("home") { HomeScreen(navController) }

                        composable("logvitals") { LogVitalsScreen(navController) }

                        // Plain route (defaults) — safe for bottom bar taps
                        composable("healthtips") {
                            val userProfile = UserProfile(age = 30, condition = "General")
                            HealthTipsScreen(navController, userProfile)
                        }

                        // Parameterized route — used by LogVitalsScreen navigation
                        composable(
                            route = "healthtips/{age}/{condition}",
                            arguments = listOf(
                                navArgument("age") { type = NavType.IntType },
                                navArgument("condition") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val age = backStackEntry.arguments?.getInt("age") ?: 30
                            val condition = backStackEntry.arguments?.getString("condition")
                            val userProfile = UserProfile(age = age, condition = condition)
                            HealthTipsScreen(navController, userProfile)
                        }

                        composable("profile") {
                            ProfileScreen(
                                isDarkMode = isDarkMode,
                                onToggleTheme = { enabled ->
                                    isDarkMode = enabled
                                    scope.launch { themePrefs.setDarkMode(enabled) }
                                },
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate("login") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
