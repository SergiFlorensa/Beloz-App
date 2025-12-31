package com.app.beloz.innovacion.contexto.datos

import com.app.beloz.BuildConfig
import com.app.beloz.innovacion.contexto.datos.api.OpenWeatherApi
import com.app.beloz.innovacion.contexto.datos.modelos.OpenWeatherResponse
import com.app.beloz.innovacion.contexto.dominio.ContextoClima
import com.app.beloz.innovacion.contexto.dominio.EstadoClima
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProveedorClimaRemoto(
    private val ciudadPorDefecto: String = "Reus,ES"
) {
    private val api: OpenWeatherApi by lazy { crearApi() }

    suspend fun obtenerClima(ciudad: String = ciudadPorDefecto): ContextoClima? {
        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        if (apiKey.isBlank()) {
            return null
        }
        return try {
            val respuesta = api.getCurrentWeather(city = ciudad, apiKey = apiKey)
            mapear(respuesta)
        } catch (e: Exception) {
            null
        }
    }

    private fun crearApi(): OpenWeatherApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(OpenWeatherApi::class.java)
    }

    private fun mapear(respuesta: OpenWeatherResponse): ContextoClima {
        val descripcion = respuesta.weather.firstOrNull()
        val estado = when (descripcion?.main?.lowercase()) {
            "rain", "drizzle", "thunderstorm" -> EstadoClima.LLUVIA
            "clouds" -> EstadoClima.NUBLADO
            "snow" -> EstadoClima.FRIO
            "clear" -> EstadoClima.SOLEADO
            else -> EstadoClima.DESCONOCIDO
        }
        val temperatura = respuesta.main.temperature
        val estadoFinal = if (estado == EstadoClima.SOLEADO && temperatura < 12) EstadoClima.FRIO else estado
        return ContextoClima(
            estado = estadoFinal,
            temperatura = temperatura,
            descripcion = descripcion?.description
        )
    }
}
