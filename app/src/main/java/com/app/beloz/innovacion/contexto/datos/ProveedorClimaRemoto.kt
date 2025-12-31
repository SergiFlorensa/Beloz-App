package com.app.beloz.innovacion.contexto.datos

import com.app.beloz.innovacion.contexto.datos.api.OpenMeteoApi
import com.app.beloz.innovacion.contexto.datos.modelos.OpenMeteoResponse
import com.app.beloz.innovacion.contexto.dominio.ContextoClima
import com.app.beloz.innovacion.contexto.dominio.EstadoClima
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProveedorClimaRemoto(
    private val latitud: Double = 41.1561,
    private val longitud: Double = 1.1069
) {
    private val api: OpenMeteoApi by lazy { crearApi() }

    suspend fun obtenerClima(): ContextoClima? {
        return try {
            val respuesta = api.obtenerClimaActual(latitud = latitud, longitud = longitud)
            mapear(respuesta)
        } catch (e: Exception) {
            null
        }
    }

    private fun crearApi(): OpenMeteoApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(OpenMeteoApi::class.java)
    }

    private fun mapear(respuesta: OpenMeteoResponse): ContextoClima? {
        val current = respuesta.current ?: return null
        val estado = when (current.weatherCode) {
            0 -> EstadoClima.SOLEADO
            in 1..3 -> EstadoClima.NUBLADO
            45, 48 -> EstadoClima.NUBLADO
            in 51..67, in 80..82 -> EstadoClima.LLUVIA
            in 71..77, in 85..86 -> EstadoClima.FRIO
            else -> EstadoClima.DESCONOCIDO
        }
        val temperatura = current.temperature
        val estadoFinal = if (estado == EstadoClima.SOLEADO && (temperatura ?: 0.0) < 12) EstadoClima.FRIO else estado
        return ContextoClima(
            estado = estadoFinal,
            temperatura = temperatura,
            descripcion = descripcionPorCodigo(current.weatherCode)
        )
    }

    private fun descripcionPorCodigo(codigo: Int?): String? = when (codigo) {
        0 -> "Cielo despejado"
        1, 2 -> "Parcialmente nublado"
        3 -> "Nublado"
        in 45..48 -> "Niebla"
        in 51..57 -> "Llovizna"
        in 61..67 -> "Lluvia"
        in 71..77 -> "Nieve"
        in 80..82 -> "Chubascos"
        in 85..86 -> "Nieve intensa"
        else -> null
    }
}
