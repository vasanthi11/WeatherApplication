package com.dev.weatherapp.model

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("data/2.5/weather?")
    suspend fun getWeatherReport(
        @Query("apikey") apikey: String = "b67af2e321e57e6754d055ee78a99348",
        @Query("lat") lat: String?,
        @Query("lon") lng: String?
    ): WeatherResponse?


    @GET("data/2.5/weather?")
    suspend fun getWeather(
        @Query("q") city: String?,
        @Query("apikey") apikey: String = "b67af2e321e57e6754d055ee78a99348",
    ): WeatherResponse?

    @GET("data/2.5/weather?q={city},{countrycode}&appid={apikey}")
    suspend fun getWeather(
        @Path("apikey") apikey: String = "b67af2e321e57e6754d055ee78a99348",
        @Path("city") city: String?,
        @Path("countrycode") countryCOde : String
    ): WeatherResponse


    @GET("data/2.5/weather?q={city},{statecode},{countrycode}&appid={apikey}")
    suspend fun getWeather(
        @Path("apikey") apikey: String = "b67af2e321e57e6754d055ee78a99348",
        @Path("city") city: String?,
        @Path("countrycode") countryCOde : String,
        @Path("statecode") startCode : String
    ): WeatherResponse


}