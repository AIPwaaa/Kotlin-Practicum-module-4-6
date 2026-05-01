package com.example.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Calendar
import android.Manifest
import android.content.ContentValues
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.system.Os.close
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Path
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.autofill.ContentDataType.Companion.Date
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.io.File
import java.util.Date
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import coil.compose.AsyncImage
import com.example.myapplication.data.local.AppDatabase
import com.example.myapplication.data.local.TodoJsonDataSource
import com.example.myapplication.data.preferences.UserPreferencesRepository
import com.example.myapplication.data.repository.TodoRepositoryImpl
import com.example.myapplication.domain.usecase.AddTaskUseCase
import com.example.myapplication.domain.usecase.DeleteTaskUseCase
import com.example.myapplication.domain.usecase.GetTodosUseCase
import com.example.myapplication.domain.usecase.UpdateTaskUseCase
import com.example.myapplication.presentation.ui.screen.TodoDetailScreen
import com.example.myapplication.presentation.ui.screen.TodoListScreen
import com.example.myapplication.presentation.viewmodel.TodoViewModel
import java.util.Locale
import java.text.SimpleDateFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Инициализируем зависимости (Data Layer)
        val database = AppDatabase.getDatabase(this)
        val dao = database.taskDao()
        val repository = TodoRepositoryImpl(dao)
        val jsonDataSource = TodoJsonDataSource(this)
        val userPrefs = UserPreferencesRepository(this)

        // 2. Создаем Use Cases (Domain Layer)
        val getTodosUseCase = GetTodosUseCase(repository)
        val addTaskUseCase = AddTaskUseCase(repository)
        val updateTaskUseCase = UpdateTaskUseCase(repository)
        val deleteTaskUseCase = DeleteTaskUseCase(repository)

        // 3. Создаем ViewModel через Factory
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TodoViewModel(
                    getTodosUseCase,
                    addTaskUseCase,
                    updateTaskUseCase,
                    deleteTaskUseCase,
                    userPrefs,
                    repository,
                    jsonDataSource
                ) as T
            }
        }

        val viewModel = ViewModelProvider(this, viewModelFactory)[TodoViewModel::class.java]

        setContent {
            // 1. Создаем контроллер навигации
            val navController = rememberNavController()
            // 2. Подписываемся на состояние данных из ViewModel
            val state by viewModel.uiState.collectAsState()

            NavHost(
                navController = navController,
                startDestination = "todo_list"
            ) {
                // Экран списка
                composable("todo_list") {
                    TodoListScreen(
                        viewModel = viewModel,
                        onItemClick = { task ->
                            // Переходим на детали, передавая ID задачи
                            navController.navigate("todo_detail/${task.id}")
                        }
                    )
                }

                // Экран деталей
                composable("todo_detail/{taskId}") { backStackEntry ->
                    // Получаем ID из строки пути
                    val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()

                    // Ищем задачу в текущем списке по ID
                    val taskItem = state.tasks.find { it.id == taskId }

                    if (taskItem != null) {
                        TodoDetailScreen(
                            item = taskItem,
                            onBack = { navController.popBackStack() }
                        )
                    } else {
                        // Если задача не найдена (например, ошибка базы), просто выходим назад
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}