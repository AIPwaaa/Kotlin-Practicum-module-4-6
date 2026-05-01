package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>

    suspend fun addTask(item: TodoItem)
    suspend fun updateTask(item: TodoItem)
    suspend fun deleteTask(item: TodoItem)
}