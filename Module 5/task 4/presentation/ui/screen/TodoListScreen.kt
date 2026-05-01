package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.TodoItem
import com.example.myapplication.presentation.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    viewModel: TodoViewModel,
    onItemClick: (TodoItem) -> Unit
) {
    // Собираем состояние из ViewModel
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список дел") },
                actions = {
                    // Тот самый switcher из задания
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Цвет завершенных", style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = state.isColorEnabled,
                            onCheckedChange = { viewModel.toggleColorPreference(it) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.addTask(TodoItem(title = "Новая задача", description = "Из Room"))
            }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        if (state.tasks.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Задач нет")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(state.tasks) { task ->
                    // Выносим отдельный компонент для элемента списка
                    TodoRow(
                        item = task,
                        isColorEnabled = state.isColorEnabled,
                        onClick = { onItemClick(task) },
                        onCheckedChange = { viewModel.onTaskCheckedChange(task) },
                        onDeleteClick = {
                            viewModel.deleteTask(task)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TodoRow(
    item: TodoItem,
    isColorEnabled: Boolean,
    onClick: () -> Unit,
    onCheckedChange: () -> Unit,
    onDeleteClick: () -> Unit // Добавляем новый параметр
) {
    val cardColor = if (isColorEnabled && item.isCompleted) {
        CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9))
    } else {
        CardDefaults.cardColors()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onClick,
        colors = cardColor
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                Text(text = item.description, style = MaterialTheme.typography.bodySmall)
            }

            // Кнопка удаления (иконка корзины)
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Color.Gray
                )
            }

            Checkbox(
                checked = item.isCompleted,
                onCheckedChange = { onCheckedChange() }
            )
        }
    }
}