package com.example.myapplication.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.domain.model.User
import com.example.myapplication.presentation.viewmodel.AuthViewModel
import com.example.myapplication.presentation.viewmodel.UIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: Int,
    viewModel: AuthViewModel,
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.userDetailState.collectAsState()

    LaunchedEffect(userId) {
        viewModel.loadUserDetail(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is UIState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UIState.Success -> {
                    val user = (state as UIState.Success<User>).data
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = user.image,
                            contentDescription = null,
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("${user.firstName} ${user.lastName}", style = MaterialTheme.typography.headlineLarge)
                        Text(user.email, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = {
                                viewModel.logout()
                                onLogout()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Выйти")
                        }
                    }
                }
                is UIState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((state as UIState.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadUserDetail(userId) }) { Text("Повторить") }
                    }
                }
                else -> {}
            }
        }
    }
}
