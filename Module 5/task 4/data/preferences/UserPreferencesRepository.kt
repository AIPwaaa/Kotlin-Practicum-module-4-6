package com.example.myapplication.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Создаем делегат для доступа к хранилищу
private val Context.dataStore by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    // Ключ, по которому будем хранить "включен ли цвет"
    private val IS_COLOR_ENABLED = booleanPreferencesKey("is_color_enabled")

    // Читаем значение (Flow позволяет UI реагировать на изменения мгновенно)
    val isColorEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_COLOR_ENABLED] ?: false // По умолчанию false
        }

    // Записываем значение
    suspend fun updateColorPreference(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_COLOR_ENABLED] = isEnabled
        }
    }
}