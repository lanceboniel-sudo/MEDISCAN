package com.example.mediscanmain

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.mediscanmain.ui.auth.LoginScreen
import com.example.mediscanmain.ui.auth.RegisterScreen
import com.example.mediscanmain.ui.home.HomeScreen
import com.example.mediscanmain.ui.scan.ScanScreen
import com.example.mediscanmain.ui.profile.ProfileScreen
import com.example.mediscanmain.ui.components.BottomBar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("FirebaseTest", "Current user: ${FirebaseAuth.getInstance().currentUser?.email}")
        super.onCreate(savedInstanceState)

        val bottomBarRoutes = listOf("home", "logvitals", "healthtips", "profile")

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }
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
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(navController)
                        }

                        composable("logvitals") {
                            ScanScreen(
                                onScanSaved = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("healthtips") {
                            // Replace with your actual HealthTipsScreen
                            Text("Health Tips Screen")
                        }

                        composable("profile") {
                            ProfileScreen(
                                isDarkMode = isDarkMode,
                                onToggleTheme = { isDarkMode = it },
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
