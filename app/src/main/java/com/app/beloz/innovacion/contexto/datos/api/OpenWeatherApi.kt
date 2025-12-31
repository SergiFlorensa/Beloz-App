package com.app.beloz.innovacion.contexto.datos.api

import com.app.beloz.innovacion.contexto.datos.modelos.OpenWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "es"
    ): OpenWeatherResponse
}
