package com.example.myapplication.data.remote

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.OutputStream

class PhotoDownloader(private val context: Context) {
    private val client = OkHttpClient()

    suspend fun downloadPhoto(url: String, targetUri: Uri): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) return@withContext Result.failure(Exception("Failed to download image"))

                val inputStream = response.body?.byteStream() ?: return@withContext Result.failure(Exception("Empty response body"))
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(targetUri)

                if (outputStream == null) return@withContext Result.failure(Exception("Failed to open output stream"))

                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
