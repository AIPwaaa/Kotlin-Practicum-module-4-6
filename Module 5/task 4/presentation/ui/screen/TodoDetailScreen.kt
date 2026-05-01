package com.example.myapplication.presentation.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.domain.model.TodoItem

@Composable
fun TodoDetailScreen(
    item: TodoItem,
    onBack: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        Text(item.title)
        Text(item.description)
        Text(if (item.isCompleted) "Выполнено" else "Не выполнено")
        Button(onClick = onBack) {
            Text("Назад")
        }
    }
}
