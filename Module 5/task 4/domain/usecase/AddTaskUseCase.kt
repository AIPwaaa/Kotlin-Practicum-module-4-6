package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.TodoItem
import com.example.myapplication.domain.repository.TodoRepository

class AddTaskUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(item: TodoItem) = repository.addTask(item)
}