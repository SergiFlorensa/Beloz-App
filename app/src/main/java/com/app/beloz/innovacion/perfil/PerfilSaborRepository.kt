package com.app.beloz.innovacion.perfil

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.perfilDataStore by preferencesDataStore(name = "perfil_sabor")

class PerfilSaborRepository(private val context: Context) {
    private val gson = Gson()
    private val eventosKey = stringPreferencesKey("perfil_eventos_json")
    private val maxEventos = 200

    suspend fun registrarEvento(evento: EventoUso) {
        context.perfilDataStore.edit { prefs ->
            val actuales = leerEventos(prefs[eventosKey])
            val nuevos = (actuales + evento).takeLast(maxEventos)
            prefs[eventosKey] = gson.toJson(nuevos)
        }
    }

    fun perfilFlow(): Flow<PerfilSabor> {
        return context.perfilDataStore.data.map { prefs ->
            val eventos = leerEventos(prefs[eventosKey])
            PerfilSabor.fromEventos(eventos)
        }
    }

    private fun leerEventos(json: String?): List<EventoUso> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            val tipo = object : TypeToken<List<EventoUso>>() {}.type
            gson.fromJson<List<EventoUso>>(json, tipo) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
