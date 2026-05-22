package com.example.myapplication.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.Laureate
import com.example.myapplication.domain.model.NobelPrize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NobelDetailScreen(
    laureate: Laureate,
    prize: NobelPrize,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали лауреата") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (laureate.portraitUrl != null) {
                AsyncImage(
                    model = laureate.portraitUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(text = laureate.fullName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Год: ${prize.year}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Категория: ${prize.category}", style = MaterialTheme.typography.titleMedium)
            
            if (laureate.birthCountry != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Страна: ${laureate.birthCountry}", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Описание (Motivation):", fontWeight = FontWeight.Bold)
            Text(text = laureate.motivation, style = MaterialTheme.typography.bodyLarge)
        }
    }
}
