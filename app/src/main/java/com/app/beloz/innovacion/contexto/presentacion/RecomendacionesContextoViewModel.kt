package com.app.beloz.innovacion.contexto.presentacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.beloz.innovacion.contexto.datos.ProveedorClimaRemoto
import com.app.beloz.innovacion.contexto.datos.ProveedorContextoLocal
import com.app.beloz.innovacion.contexto.dominio.ContextoClima
import com.app.beloz.innovacion.contexto.dominio.ContextoEntrada
import com.app.beloz.innovacion.contexto.dominio.EstadoClima
import com.app.beloz.innovacion.contexto.dominio.MotorRecomendacionesContextuales
import com.app.beloz.innovacion.contexto.dominio.SugerenciaContextual
import com.app.beloz.innovacion.contexto.dominio.TipoDeDia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecomendacionesContextoViewModel(
    private val proveedorContexto: ProveedorContextoLocal = ProveedorContextoLocal(),
    private val proveedorClima: ProveedorClimaRemoto = ProveedorClimaRemoto(),
    private val motor: MotorRecomendacionesContextuales = MotorRecomendacionesContextuales.motorPorDefecto()
) : ViewModel() {

    private val _estado = MutableStateFlow(RecomendacionesContextoUiState())
    val estado: StateFlow<RecomendacionesContextoUiState> = _estado

    init {
        refrescar()
    }

    fun refrescar() {
        viewModelScope.launch {
            _estado.value = _estado.value.copy(cargando = true, error = null)
            val contextoBase = proveedorContexto.obtenerContexto()
            val clima = proveedorClima.obtenerClima()
            val contexto = contextoBase.copy(clima = clima)
            val sugerencias = motor.generar(contexto)
            _estado.value = RecomendacionesContextoUiState(
                sugerencias = sugerencias,
                descripcionContextual = construirDescripcion(contexto),
                descripcionClima = construirDescripcionClima(clima),
                hayClima = clima != null,
                cargando = false,
                error = if (clima == null) "No se pudo obtener el clima en este momento." else null
            )
        }
    }

    private fun construirDescripcion(contexto: ContextoEntrada): String {
        val momento = contexto.momentoDelDia.name.lowercase().replaceFirstChar { it.uppercase() }
        val tipoDia = if (contexto.tipoDeDia == TipoDeDia.FIN_DE_SEMANA) "fin de semana" else "dia laborable"
        return "$momento - ${contexto.diaDeLaSemana.name.lowercase()} - $tipoDia"
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
}

data class RecomendacionesContextoUiState(
    val sugerencias: List<SugerenciaContextual> = emptyList(),
    val descripcionContextual: String = "",
    val descripcionClima: String = "",
    val hayClima: Boolean = false,
    val cargando: Boolean = true,
    val error: String? = null
)
