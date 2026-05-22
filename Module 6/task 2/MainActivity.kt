package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.repository.NobelRepositoryImpl
import com.example.myapplication.domain.model.Laureate
import com.example.myapplication.domain.model.NobelPrize
import com.example.myapplication.domain.usecase.GetNobelPrizesUseCase
import com.example.myapplication.presentation.ui.NobelDetailScreen
import com.example.myapplication.presentation.ui.NobelListScreen
import com.example.myapplication.presentation.viewmodel.NobelState
import com.example.myapplication.presentation.viewmodel.NobelViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual DI with Ktor
        val client = HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
        val repository = NobelRepositoryImpl(client)
        val getNobelPrizesUseCase = GetNobelPrizesUseCase(repository)

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return NobelViewModel(getNobelPrizesUseCase) as T
            }
        }

        setContent {
            val navController = rememberNavController()
            val viewModel: NobelViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = viewModelFactory)
            
            var selectedLaureate by remember { mutableStateOf<Pair<Laureate, NobelPrize>?>(null) }

            NavHost(navController = navController, startDestination = "nobel_list") {
                composable("nobel_list") {
                    NobelListScreen(
                        viewModel = viewModel,
                        onLaureateClick = { laureate, prize ->
                            selectedLaureate = Pair(laureate, prize)
                            navController.navigate("nobel_detail")
                        }
                    )
                }
                composable("nobel_detail") {
                    val pair = selectedLaureate
                    if (pair != null) {
                        NobelDetailScreen(
                            laureate = pair.first,
                            prize = pair.second,
                            onBack = { navController.popBackStack() }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
