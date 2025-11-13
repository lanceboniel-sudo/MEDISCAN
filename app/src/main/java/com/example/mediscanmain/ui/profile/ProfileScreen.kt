package com.example.mediscanmain.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    isDarkMode: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onLogout: () -> Unit
) {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()

    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(0) }
    var gender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var isEditing by remember { mutableStateOf(false) }
    var editedFirstName by remember { mutableStateOf("") }
    var editedLastName by remember { mutableStateOf("") }
    var editedAge by remember { mutableStateOf("") }
    var editedGender by remember { mutableStateOf("") }

    var isDarkMode by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        firstname = document.getString("firstname") ?: ""
                        lastname = document.getString("lastname") ?: ""
                        age = document.getLong("age")?.toInt() ?: 0
                        gender = document.getString("gender") ?: ""
                        email = document.getString("email") ?: ""

                        editedFirstName = firstname
                        editedLastName = lastname
                        editedAge = age.toString()
                        editedGender = gender
                    }
                }
        }
    }

    val themeColors = if (isDarkMode) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = themeColors) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("👤 Profile", style = MaterialTheme.typography.headlineMedium)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (isEditing) {
                            OutlinedTextField(
                                value = editedFirstName,
                                onValueChange = { editedFirstName = it },
                                label = { Text("First Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editedLastName,
                                onValueChange = { editedLastName = it },
                                label = { Text("Last Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = editedAge,
                                onValueChange = { editedAge = it.filter { c -> c.isDigit() } },
                                label = { Text("Age") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("Gender")
                            listOf("Male", "Female", "Other").forEach { option ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = editedGender == option,
                                        onClick = { editedGender = option }
                                    )
                                    Text(option)
                                }
                            }

                            Button(
                                onClick = {
                                    val updatedData = mapOf(
                                        "firstname" to editedFirstName,
                                        "lastname" to editedLastName,
                                        "age" to editedAge.toIntOrNull(),
                                        "gender" to editedGender
                                    )
                                    uid?.let {
                                        firestore.collection("users").document(it).update(updatedData)
                                            .addOnSuccessListener {
                                                firstname = editedFirstName
                                                lastname = editedLastName
                                                age = editedAge.toIntOrNull() ?: age
                                                gender = editedGender
                                                isEditing = false
                                            }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Save Changes")
                            }
                        } else {
                            ProfileRow(Icons.Default.Person, "Name", "$firstname $lastname")
                            ProfileRow(Icons.Default.Cake, "Age", "$age")
                            ProfileRow(Icons.Default.Wc, "Gender", gender)
                            ProfileRow(Icons.Default.Email, "Email", email)

                            Button(
                                onClick = { isEditing = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Edit Profile")
                            }
                        }
                    }
                }

                Divider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dark Mode")
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { isDarkMode = it }
                    )
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun ProfileRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = label)
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
