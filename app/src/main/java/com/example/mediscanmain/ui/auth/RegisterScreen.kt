package com.example.mediscanmain.ui.auth

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.navigation.NavController
import com.example.mediscanmain.ui.profile.ProfileSetupScreen

@Composable
fun RegisterScreen(
    navController: NavController,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showProfileSetup by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    if (showProfileSetup) {
        ProfileSetupScreen(onSubmit = { firstName, lastName, age, gender ->
            val uid = auth.currentUser?.uid
            val userEmail = auth.currentUser?.email

            if (uid != null && userEmail != null) {
                val userData = hashMapOf(
                    "firstname" to firstName,
                    "lastname" to lastName,
                    "age" to age,
                    "gender" to gender,
                    "email" to userEmail
                )

                firestore.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        navController.navigate("home") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                    .addOnFailureListener { e ->
                        errorMessage = "Failed to save profile: ${e.localizedMessage}"
                    }
            } else {
                errorMessage = "User not authenticated"
            }
        })
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email).matches(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage = "Invalid email format"
                } else if (password != confirmPassword) {
                    errorMessage = "Passwords do not match"
                } else {
                    scope.launch {
                        try {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showProfileSetup = true
                                    } else {
                                        errorMessage = task.exception?.localizedMessage ?: "Registration failed"
                                    }
                                }
                        } catch (e: Exception) {
                            errorMessage = e.localizedMessage
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateBack) {
            Text("Back to Login")
        }
    }
}
