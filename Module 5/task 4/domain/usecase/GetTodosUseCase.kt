package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.TodoItem
import com.example.myapplication.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<TodoItem>> = repository.getTodos()
}