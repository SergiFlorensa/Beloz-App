package com.app.beloz.innovacion.contexto.dominio

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

/**
 * Representa los datos minimos que podemos obtener localmente sin depender de la red.
 */
data class ContextoEntrada(
    @SerializedName("momento_del_dia")
    val momentoDelDia: MomentoDelDia,
    @SerializedName("tipo_de_dia")
    val tipoDeDia: TipoDeDia,
    @SerializedName("dia_de_la_semana")
    val diaDeLaSemana: DayOfWeek,
    val clima: ContextoClima? = null,
    @SerializedName("perfil_sabor")
    val perfilSabor: PerfilSaborContextual? = null,
)

data class ContextoClima(
    val estado: EstadoClima,
    val temperatura: Double?,
    val descripcion: String?,
)

data class PerfilSaborContextual(
    @SerializedName("total_eventos")
    val totalEventos: Int = 0,
    @SerializedName("top_tipos_comida")
    val topTiposComida: List<ConteoContextual> = emptyList(),
    @SerializedName("top_rangos_precio")
    val topRangosPrecio: List<ConteoContextual> = emptyList(),
    @SerializedName("top_restaurantes")
    val topRestaurantes: List<ConteoContextual> = emptyList()
)

data class ConteoContextual(
    val clave: String,
    val conteo: Int
)

enum class EstadoClima {
    SOLEADO,
    NUBLADO,
    LLUVIA,
    FRIO,
    DESCONOCIDO;
}

enum class MomentoDelDia {
    MADRUGADA,
    MANHANA,
    MEDIODIA,
    TARDE,
    NOCHE;
}

enum class TipoDeDia {
    LABORABLE,
    FIN_DE_SEMANA;
}
