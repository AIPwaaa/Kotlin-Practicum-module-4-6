package com.example.myapplication.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.Laureate
import com.example.myapplication.domain.model.NobelPrize
import com.example.myapplication.presentation.viewmodel.NobelState
import com.example.myapplication.presentation.viewmodel.NobelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NobelListScreen(
    viewModel: NobelViewModel,
    onLaureateClick: (Laureate, NobelPrize) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var yearText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    val categories = listOf("all", "physics", "chemistry", "medicine", "literature", "peace", "economics")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Нобелевские лауреаты") })
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Filters
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = yearText,
                    onValueChange = { yearText = it },
                    label = { Text("Год") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text(selectedCategory)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                IconButton(onClick = {
                    viewModel.loadPrizes(yearText.toIntOrNull(), selectedCategory)
                }) {
                    Text("OK")
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (val currentState = state) {
                    is NobelState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is NobelState.Success -> {
                        LazyColumn {
                            items(currentState.prizes) { prize ->
                                prize.laureates.forEach { laureate ->
                                    LaureateItem(
                                        laureate = laureate,
                                        prize = prize,
                                        onClick = { onLaureateClick(laureate, prize) }
                                    )
                                }
                            }
                        }
                    }
                    is NobelState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
                            Button(onClick = { viewModel.loadPrizes() }) {
                                Text("Повторить")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LaureateItem(laureate: Laureate, prize: NobelPrize, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = laureate.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "${prize.year} - ${prize.category}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = laureate.motivation,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
