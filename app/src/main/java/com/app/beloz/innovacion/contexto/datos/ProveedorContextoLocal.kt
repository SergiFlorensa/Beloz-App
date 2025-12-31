package com.app.beloz.innovacion.contexto.datos

import com.app.beloz.innovacion.contexto.dominio.ContextoEntrada
import com.app.beloz.innovacion.contexto.dominio.MomentoDelDia
import com.app.beloz.innovacion.contexto.dominio.TipoDeDia
import java.time.DayOfWeek
import java.time.LocalDateTime

/**
 * Recupera información básica sin depender de la red. Se ampliará con clima y calendario.
 */
class ProveedorContextoLocal {
    fun obtenerContexto(): ContextoEntrada {
        val ahora = LocalDateTime.now()
        val momento = determinarMomento(ahora.hour)
        val tipoDia = if (esFinDeSemana(ahora.dayOfWeek)) TipoDeDia.FIN_DE_SEMANA else TipoDeDia.LABORABLE
        return ContextoEntrada(
            momentoDelDia = momento,
            tipoDeDia = tipoDia,
            diaDeLaSemana = ahora.dayOfWeek
        )
    }

    private fun determinarMomento(hora: Int): MomentoDelDia = when (hora) {
        in 5..10 -> MomentoDelDia.MANHANA
        in 11..13 -> MomentoDelDia.MEDIODIA
        in 14..18 -> MomentoDelDia.TARDE
        in 19..23 -> MomentoDelDia.NOCHE
        else -> MomentoDelDia.MADRUGADA
    }

    private fun esFinDeSemana(dia: DayOfWeek): Boolean =
        dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY
}
