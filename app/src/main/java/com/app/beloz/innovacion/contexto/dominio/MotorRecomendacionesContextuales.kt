package com.app.beloz.innovacion.contexto.dominio

/**
 * Motor sencillo basado en reglas, preparado para anadir modelos IA posteriormente.
 */
class MotorRecomendacionesContextuales(
    private val reglas: List<ReglaContextual>
) {
    fun generar(contexto: ContextoEntrada): List<SugerenciaContextual> {
        return reglas
            .sortedByDescending { it.peso }
            .mapNotNull { it.evaluar(contexto) }
    }

    companion object {
        fun motorPorDefecto(): MotorRecomendacionesContextuales {
            val reglas = listOf(
                ReglaContextual(
                    nombre = "Domingo confortable",
                    condicion = { ctx ->
                        ctx.diaDeLaSemana == java.time.DayOfWeek.SUNDAY && ctx.momentoDelDia == MomentoDelDia.NOCHE
                    },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Domingo lluvioso = comida reconfortante",
                            descripcion = "Prueba una pizza artesanal o un postre con chocolate caliente.",
                            etiquetas = listOf("comfort", "sabores intensos"),
                            motivo = "Domingo por la noche y necesitas algo reconfortante."
                        )
                    },
                    peso = 3
                ),
                ReglaContextual(
                    nombre = "Mediodia ejecutivo",
                    condicion = { ctx ->
                        ctx.momentoDelDia == MomentoDelDia.MEDIODIA && ctx.tipoDeDia == TipoDeDia.LABORABLE
                    },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Necesitas energia rapida",
                            descripcion = "Explora menus ejecutivos o ensaladas proteicas que llegan en <20 min.",
                            etiquetas = listOf("rapido", "ligero"),
                            motivo = "Mediodia laboral con poco tiempo."
                        )
                    },
                    peso = 2
                ),
                ReglaContextual(
                    nombre = "Tarde creativa",
                    condicion = { ctx -> ctx.momentoDelDia == MomentoDelDia.TARDE },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Dale un twist a la merienda",
                            descripcion = "Combina bowls frutales con cafes de especialidad o bubble tea.",
                            etiquetas = listOf("snack", "creativo"),
                            motivo = "La tarde es ideal para algo ligero y creativo."
                        )
                    },
                    peso = 1
                ),
                ReglaContextual(
                    nombre = "Clima lluvioso",
                    condicion = { ctx -> ctx.clima?.estado == EstadoClima.LLUVIA },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Que la lluvia no te frene",
                            descripcion = "Sopas asiaticas, ramen o curry especiado para entrar en calor.",
                            etiquetas = listOf("caliente", "spicy"),
                            motivo = "Se detecto lluvia en tu zona."
                        )
                    },
                    peso = 4
                ),
                ReglaContextual(
                    nombre = "Frio intenso",
                    condicion = { ctx -> ctx.clima?.estado == EstadoClima.FRIO },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Activa tu termostato",
                            descripcion = "Guisos, fondue y postres calientes para reconfortar el cuerpo.",
                            etiquetas = listOf("invierno", "hogareno"),
                            motivo = "Temperatura baja detectada."
                        )
                    },
                    peso = 4
                )
            )
            return MotorRecomendacionesContextuales(reglas)
        }
    }
}
