package com.dev.weatherapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dev.weatherapp.model.DataRepository
import com.dev.weatherapp.model.RetrofitBuilder

class CustomViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(DataRepository(RetrofitBuilder.apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}