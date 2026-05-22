package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.remote.PhotoDownloader
import com.example.myapplication.data.remote.PicsumApi
import com.example.myapplication.data.repository.PhotoRepositoryImpl
import com.example.myapplication.domain.usecase.GetPhotosUseCase
import com.example.myapplication.presentation.ui.PhotoDetailScreen
import com.example.myapplication.presentation.ui.PhotoListScreen
import com.example.myapplication.presentation.viewmodel.PhotoListState
import com.example.myapplication.presentation.viewmodel.PhotoViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual DI
        val retrofit = Retrofit.Builder()
            .baseUrl(PicsumApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(PicsumApi::class.java)
        val repository = PhotoRepositoryImpl(api)
        val getPhotosUseCase = GetPhotosUseCase(repository)
        val downloader = PhotoDownloader(this)

        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return PhotoViewModel(getPhotosUseCase) as T
            }
        }

        setContent {
            val navController = rememberNavController()
            val viewModel: PhotoViewModel = viewModel(factory = viewModelFactory)
            val scope = rememberCoroutineScope()
            
            var selectedPhoto by remember { mutableStateOf<com.example.myapplication.domain.model.Photo?>(null) }
            
            val downloadLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.CreateDocument("image/jpeg")
            ) { uri ->
                uri?.let {
                    selectedPhoto?.let { photo ->
                        scope.launch {
                            val result = downloader.downloadPhoto(photo.downloadUrl, it)
                            if (result.isSuccess) {
                                Toast.makeText(this@MainActivity, "Фото скачано", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Ошибка скачивания", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

            NavHost(navController = navController, startDestination = "photo_list") {
                composable("photo_list") {
                    PhotoListScreen(
                        viewModel = viewModel,
                        onPhotoClick = { photo ->
                            selectedPhoto = photo
                            navController.navigate("photo_detail/${photo.id}")
                        }
                    )
                }
                composable("photo_detail/{photoId}") { backStackEntry ->
                    val photoId = backStackEntry.arguments?.getString("photoId")
                    val state by viewModel.state.collectAsState()
                    val photo = (state as? PhotoListState.Success)?.photos?.find { it.id == photoId }

                    if (photo != null) {
                        PhotoDetailScreen(
                            photo = photo,
                            onBack = { navController.popBackStack() },
                            onDownload = {
                                selectedPhoto = it
                                downloadLauncher.launch("photo_${it.id}.jpg")
                            }
                        )
                    } else {
                        // Handle case where photo is not found in state (e.g. process death)
                        LaunchedEffect(Unit) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}
