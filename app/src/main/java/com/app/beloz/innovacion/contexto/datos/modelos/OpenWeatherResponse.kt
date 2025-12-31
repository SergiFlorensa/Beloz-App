package com.app.beloz.innovacion.contexto.datos.modelos

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("main") val main: WeatherMain,
)

data class WeatherDescription(
    @SerializedName("main") val main: String,
    @SerializedName("description") val description: String,
)

data class WeatherMain(
    @SerializedName("temp") val temperature: Double,
)
