package com.app.beloz.innovacion.contexto.dominio

import java.time.DayOfWeek

/**
 * Representa los datos mínimos que podemos obtener localmente sin depender de la red.
 */
data class ContextoEntrada(
    val momentoDelDia: MomentoDelDia,
    val tipoDeDia: TipoDeDia,
    val diaDeLaSemana: DayOfWeek,
    val clima: ContextoClima? = null,
)

data class ContextoClima(
    val estado: EstadoClima,
    val temperatura: Double?,
    val descripcion: String?,
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
