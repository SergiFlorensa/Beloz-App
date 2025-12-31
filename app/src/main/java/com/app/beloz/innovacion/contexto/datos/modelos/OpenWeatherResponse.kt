package com.app.beloz.innovacion.contexto.datos.modelos

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    @SerializedName("current") val current: CurrentWeather?
)

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("weather_code") val weatherCode: Int?
)
