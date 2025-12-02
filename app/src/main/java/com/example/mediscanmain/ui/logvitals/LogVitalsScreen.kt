package com.example.mediscanmain.ui.logvitals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun LogVitalsScreen(navController: NavHostController) {
    var ageInput by remember { mutableStateOf("") }
    var bpInput by remember { mutableStateOf("") }    // blood pressure (systolic)
    var hrInput by remember { mutableStateOf("") }    // heart rate (bpm)
    var weightInput by remember { mutableStateOf("") } // kg
    var heightInput by remember { mutableStateOf("") } // cm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Log your vitals", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ageInput,
                onValueChange = { ageInput = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = bpInput,
                onValueChange = { bpInput = it },
                label = { Text("Blood Pressure (systolic, e.g., 120)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = hrInput,
                onValueChange = { hrInput = it },
                label = { Text("Heart Rate (bpm, e.g., 75)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = weightInput,
                onValueChange = { weightInput = it },
                label = { Text("Weight (kg)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = heightInput,
                onValueChange = { heightInput = it },
                label = { Text("Height (cm)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val age = ageInput.toIntOrNull() ?: 30
                val bp = bpInput.toIntOrNull() ?: 120
                val hr = hrInput.toIntOrNull() ?: 75
                val weight = weightInput.toFloatOrNull() ?: 70f
                val height = heightInput.toFloatOrNull() ?: 170f

                // Calculate BMI (kg/m^2)
                val bmi = if (height > 0f) weight / ((height / 100f) * (height / 100f)) else 0f

                // Auto-detect condition (ordered by priority)
                val condition = when {
                    bp > 130 -> "Cardio"              // high BP → cardio emphasis
                    hr > 100 -> "Aerobic"             // high HR → aerobic conditioning
                    hr < 60 -> "Strength"             // low HR → strength training
                    age >= 65 -> "Balance"            // older adults → balance activities
                    bmi >= 25 -> "Weight Management"  // overweight → weight management
                    else -> "General"
                }

                navController.navigate("healthtips/$age/$condition")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("See Health Tips")
        }
    }
}
