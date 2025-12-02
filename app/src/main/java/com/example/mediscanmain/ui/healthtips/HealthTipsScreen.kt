package com.example.mediscanmain.ui.healthtips

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mediscanmain.data.api.ApiClient
import com.example.mediscanmain.data.api.Guideline
import com.example.mediscanmain.data.model.UserProfile
import kotlinx.coroutines.launch

@Composable
fun HealthTipsScreen(navController: NavHostController, userProfile: UserProfile) {
    // Lifestyle tips (static for now, later can be dynamic based on vitals)
    val lifestyleTips = listOf(
        "Drink at least 8 glasses of water daily.",
        "Aim for 7–8 hours of sleep.",
        "Limit processed foods and sodium intake."
    )

    var guidelines by remember { mutableStateOf<List<Guideline>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userProfile) {
        scope.launch {
            try {
                val response = ApiClient.api.getGuidelines()
                if (response.isSuccessful) {
                    val allGuidelines = response.body()?.data ?: emptyList()

                    // 🔹 Filter guidelines based on age group
                    val ageFiltered = when {
                        userProfile.age < 18 -> allGuidelines.filter { it.age_group.contains("Children", ignoreCase = true) }
                        userProfile.age in 18..64 -> allGuidelines.filter { it.age_group.contains("Adults", ignoreCase = true) }
                        userProfile.age >= 65 -> allGuidelines.filter { it.age_group.contains("Older Adults", ignoreCase = true) }
                        else -> allGuidelines
                    }

                    // 🔹 Optional: filter further by condition (e.g., "Cardio", "Strength")
                    guidelines = userProfile.condition?.let { condition ->
                        ageFiltered.filter { it.activity_type.contains(condition, ignoreCase = true) }
                    } ?: ageFiltered

                } else {
                    errorMessage = "Failed to load guidelines: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Health Tips", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Lifestyle tips section
        Text("Lifestyle Tips", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        lifestyleTips.forEach { tip ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(tip, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Guidelines section
        Text("Your Official Guidelines", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> Text("Loading personalized guidelines...")
            errorMessage != null -> Text(errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error)
            guidelines.isEmpty() -> Text("No guidelines available for your profile.")
            else -> {
                LazyColumn {
                    items(guidelines) { g ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Age Group: ${g.age_group}", style = MaterialTheme.typography.titleMedium)
                                Text("Activity: ${g.activity_type}")
                                Text("Recommendation: ${g.recommendation}")
                            }
                        }
                    }
                }
            }
        }
    }
}
