package com.dev.weatherapp.model

import kotlinx.coroutines.flow.flow

class DataRepository(private val mApiService: ApiService) {

    internal fun getWeather(lat : String, lan: String, apikey : String) = flow{
        this.emit(mApiService.getWeatherReport(lat = lat, lng = lan, apikey = apikey))
    }

    internal fun getWeather(cityName : String, apikey : String) = flow{
        this.emit(mApiService.getWeather(city = cityName, apikey = apikey))
    }

}