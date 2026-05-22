package com.example.myapplication.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun UsersListScreen(
    viewModel: AuthViewModel,
    onUserClick: (Int) -> Unit
) {
    val state by viewModel.usersState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Пользователи") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state) {
                is UIState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is UIState.Success -> {
                    val users = (state as UIState.Success<List<User>>).data
                    LazyColumn {
                        items(users) { user ->
                            UserItem(user = user, onClick = { onUserClick(user.id) })
                        }
                    }
                }
                is UIState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text((state as UIState.Error).message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadUsers() }) { Text("Повторить") }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun UserItem(user: User, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text("${user.firstName} ${user.lastName}") },
        supportingContent = { Text("${user.username} | ${user.email}") },
        leadingContent = {
            AsyncImage(
                model = user.image,
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }
    )
}
