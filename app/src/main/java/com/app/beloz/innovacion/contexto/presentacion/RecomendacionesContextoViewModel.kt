package com.app.beloz.innovacion.contexto.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.innovacion.contexto.datos.ProveedorClimaRemoto
import com.app.beloz.innovacion.contexto.datos.ProveedorContextoLocal
import com.app.beloz.innovacion.contexto.datos.ProveedorRecomendacionesRemoto
import com.app.beloz.innovacion.contexto.dominio.ContextoClima
import com.app.beloz.innovacion.contexto.dominio.ContextoEntrada
import com.app.beloz.innovacion.contexto.dominio.ConteoContextual
import com.app.beloz.innovacion.contexto.dominio.EstadoClima
import com.app.beloz.innovacion.contexto.dominio.MotorRecomendacionesContextuales
import com.app.beloz.innovacion.contexto.dominio.MomentoDelDia
import com.app.beloz.innovacion.contexto.dominio.PerfilSaborContextual
import com.app.beloz.innovacion.contexto.dominio.SugerenciaContextual
import com.app.beloz.innovacion.contexto.dominio.TipoDeDia
import com.app.beloz.innovacion.perfil.PerfilSabor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek

class RecomendacionesContextoViewModel(
    private val proveedorContexto: ProveedorContextoLocal = ProveedorContextoLocal(),
    private val proveedorClima: ProveedorClimaRemoto = ProveedorClimaRemoto(),
    private val proveedorRemoto: ProveedorRecomendacionesRemoto = ProveedorRecomendacionesRemoto(),
    private val motor: MotorRecomendacionesContextuales = MotorRecomendacionesContextuales.motorPorDefecto()
) : ViewModel() {

    private val _estado = MutableStateFlow(RecomendacionesContextoUiState())
    val estado: StateFlow<RecomendacionesContextoUiState> = _estado

    init {
        refrescar()
    }

    fun refrescar(perfilSabor: PerfilSabor? = null) {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true, error = null)
            val contextoBase = proveedorContexto.obtenerContexto()
            val clima = proveedorClima.obtenerClima()
            val contexto = contextoBase.copy(
                clima = clima,
                perfilSabor = perfilSabor?.toContextual()
            )
            
            val sugerenciasLocales = motor.generar(contexto)
            val sugerenciasRemotas = proveedorRemoto.obtenerRecomendaciones(contexto)
            
            val sugerenciasTotales = (sugerenciasRemotas + sugerenciasLocales)
                .distinctBy { it.restauranteId?.toString() ?: it.titulo }
                .take(5)

            _estado.value = RecomendacionesContextoUiState(
                sugerencias = sugerenciasTotales,
                descripcionContextual = construirDescripcion(contexto),
                descripcionClima = construirDescripcionClima(clima),
                hayClima = clima != null,
                cargando = false,
                error = if (clima == null) "No se pudo obtener el clima en este momento." else null
            )
        }
    }

    private fun construirDescripcion(contexto: ContextoEntrada): String {
        val momento = contexto.momentoDelDia.toDisplayName()
        val dia = contexto.diaDeLaSemana.toDisplayName()
        val tipoDia = if (contexto.tipoDeDia == TipoDeDia.FIN_DE_SEMANA) "fin de semana" else "dia laborable"
        return "$momento - $dia - $tipoDia"
    }

    private fun construirDescripcionClima(clima: ContextoClima?): String {
        clima ?: return ""
        val estado = when (clima.estado) {
            EstadoClima.LLUVIA -> "lluvioso"
            EstadoClima.NUBLADO -> "nublado"
            EstadoClima.SOLEADO -> "soleado"
            EstadoClima.FRIO -> "frio"
            EstadoClima.DESCONOCIDO -> "variable"
        }
        val temp = clima.temperatura?.let { String.format("%.1f C", it) } ?: ""
        val desc = clima.descripcion?.replaceFirstChar { it.uppercase() } ?: estado
        return listOf(desc, temp).filter { it.isNotBlank() }.joinToString(" - ")
    }

    private fun PerfilSabor.toContextual(): PerfilSaborContextual {
        return PerfilSaborContextual(
            totalEventos = totalEventos,
            topTiposComida = topTiposComida.map { ConteoContextual(it.clave, it.conteo) },
            topRangosPrecio = topRangosPrecio.map { ConteoContextual(it.clave, it.conteo) },
            topRestaurantes = topRestaurantes.map { ConteoContextual(it.clave, it.conteo) }
        )
    }
}

private fun MomentoDelDia.toDisplayName(): String = when (this) {
    MomentoDelDia.MADRUGADA -> "Madrugada"
    MomentoDelDia.MANHANA -> "Manana"
    MomentoDelDia.MEDIODIA -> "Mediodia"
    MomentoDelDia.TARDE -> "Tarde"
    MomentoDelDia.NOCHE -> "Noche"
}

private fun DayOfWeek.toDisplayName(): String = when (this) {
    DayOfWeek.MONDAY -> "lunes"
    DayOfWeek.TUESDAY -> "martes"
    DayOfWeek.WEDNESDAY -> "miercoles"
    DayOfWeek.THURSDAY -> "jueves"
    DayOfWeek.FRIDAY -> "viernes"
    DayOfWeek.SATURDAY -> "sabado"
    DayOfWeek.SUNDAY -> "domingo"
}

data class RecomendacionesContextoUiState(
    val sugerencias: List<SugerenciaContextual> = emptyList(),
    val descripcionContextual: String = "",
    val descripcionClima: String = "",
    val hayClima: Boolean = false,
    val cargando: Boolean = true,
    val error: String? = null
)
