package com.example.myapplication.data.repository

import com.example.myapplication.data.local.TaskDao
import com.example.myapplication.data.local.TodoJsonDataSource
import com.example.myapplication.data.model.TaskEntity
import com.example.myapplication.domain.model.TodoItem
import com.example.myapplication.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
    private val taskDao: TaskDao
) : TodoRepository {

    override fun getTodos(): Flow<List<TodoItem>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { TodoItem(it.id, it.title, it.description, isCompleted = it.isCompleted) }
        }
    }

    override suspend fun addTask(item: TodoItem) {
        taskDao.insertTask(TaskEntity(title = item.title, description = item.description, isCompleted = item.isCompleted))
    }

    override suspend fun updateTask(item: TodoItem) {
        taskDao.updateTask(TaskEntity(id = item.id, title = item.title, description = item.description, isCompleted = item.isCompleted))
    }

    override suspend fun deleteTask(item: TodoItem) {
        taskDao.deleteTask(TaskEntity(id = item.id, title = item.title, description = item.description, isCompleted = item.isCompleted))
    }

    suspend fun checkAndImportData(jsonDataSource: TodoJsonDataSource) {
        val tasksFromJson = jsonDataSource.getTodosFromJson()
        tasksFromJson.forEach { entity ->
            taskDao.insertTask(entity)
        }
    }
}