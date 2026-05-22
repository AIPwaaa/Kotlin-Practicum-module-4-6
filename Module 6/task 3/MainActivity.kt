package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.local.TokenManager
import com.example.myapplication.data.repository.UserRepositoryImpl
import com.example.myapplication.domain.usecase.*
import com.example.myapplication.presentation.ui.LoginScreen
import com.example.myapplication.presentation.ui.UserDetailScreen
import com.example.myapplication.presentation.ui.UsersListScreen
import com.example.myapplication.presentation.viewmodel.AuthViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        
        // Check if token exists to decide start destination
        val initialToken = runBlocking { tokenManager.token.first() }
        val startDestination = if (initialToken != null) "users_list" else "login"

        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }

        val repository = UserRepositoryImpl(client, tokenManager)
        
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(
                    LoginUseCase(repository),
                    GetUsersUseCase(repository),
                    GetUserDetailUseCase(repository),
                    LogoutUseCase(repository)
                ) as T
            }
        }

        setContent {
            val navController = rememberNavController()
            val viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)

            NavHost(navController = navController, startDestination = startDestination) {
                composable("login") {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = {
                            navController.navigate("users_list") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
                composable("users_list") {
                    UsersListScreen(
                        viewModel = viewModel,
                        onUserClick = { userId ->
                            navController.navigate("user_detail/$userId")
                        }
                    )
                }
                composable(
                    "user_detail/{userId}",
                    arguments = listOf(navArgument("userId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                    UserDetailScreen(
                        userId = userId,
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate("login") {
                                popUpTo("users_list") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
