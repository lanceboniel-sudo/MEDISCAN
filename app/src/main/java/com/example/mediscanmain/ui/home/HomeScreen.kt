package com.example.mediscanmain.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mediscanmain.data.AppDatabase
import com.example.mediscanmain.model.ScanEntity
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    val scanDao = db.scanDao()

    var scanList by remember { mutableStateOf(emptyList<ScanEntity>()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scanDao.getAllScans().collectLatest {
            scanList = it
        }
    }

    val padding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("👋 Welcome back!", style = MaterialTheme.typography.headlineMedium)
        Text("Your Health Dashboard", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(24.dp))

        if (scanList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Latest Scan Summary", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No recent scans. Start by uploading a report or capturing a new image.")
                }
            }
        } else {
            Text("Recent Scans", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.height(8.dp))
            scanList.forEach { scan ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(scan.title, style = MaterialTheme.typography.bodyLarge)
                            Text("Saved at: ${SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(scan.timestamp)}")
                        }
                        IconButton(onClick = {
                            scope.launch(Dispatchers.IO) {
                                scanDao.delete(scan)
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("scan") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Start New Scan")
        }
    }
}
