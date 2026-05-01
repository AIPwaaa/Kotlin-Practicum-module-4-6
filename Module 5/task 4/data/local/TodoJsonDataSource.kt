package com.example.myapplication.data.local

import android.content.Context
import com.example.myapplication.data.model.TaskEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TodoJsonDataSource(private val context: Context) {
    fun getTodosFromJson(): List<TaskEntity> {
        val jsonString = context.assets.open("todos.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<TaskEntity>>() {}.type
        return Gson().fromJson(jsonString, listType)
    }
}