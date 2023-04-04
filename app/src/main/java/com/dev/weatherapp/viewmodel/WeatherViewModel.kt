package com.dev.weatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.dev.weatherapp.model.DataRepository

class WeatherViewModel(private val mDataRepository: DataRepository) : ViewModel() {
    fun getWeatherData(lat : String, lan: String, apikey : String) = mDataRepository.getWeather(lat = lat,lan = lan, apikey = apikey)

    fun getWeatherDataWithCityName(cityName : String, apikey : String) = mDataRepository.getWeather(cityName = cityName, apikey = apikey)
}