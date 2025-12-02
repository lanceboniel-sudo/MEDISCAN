package com.example.mediscanmain.ui.guidelines

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
import kotlinx.coroutines.launch

@Composable
fun GuidelinesScreen(navController: NavHostController) {
    var guidelines by remember { mutableStateOf<List<Guideline>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = ApiClient.api.getGuidelines()
                if (response.isSuccessful) {
                    guidelines = response.body()?.data ?: emptyList()
                } else {
                    errorMessage = "Failed to load guidelines: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Physical Activity Guidelines", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        when {
            errorMessage != null -> {
                Text(errorMessage ?: "Unknown error", color = MaterialTheme.colorScheme.error)
            }
            guidelines.isEmpty() -> {
                Text("Loading guidelines...")
            }
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
