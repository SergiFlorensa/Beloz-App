package com.app.beloz.innovacion.contexto.dominio

/**
 * Motor sencillo basado en reglas, preparado para añadir modelos IA posteriormente.
 */
class MotorRecomendacionesContextuales(
    private val reglas: List<ReglaContextual>
) {
    fun generar(contexto: ContextoEntrada): List<SugerenciaContextual> {
        return reglas.mapNotNull { it.evaluar(contexto) }
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
                            etiquetas = listOf("comfort", "sabores intensos")
                        )
                    }
                ),
                ReglaContextual(
                    nombre = "Mediodía ejecutivo",
                    condicion = { ctx -> ctx.momentoDelDia == MomentoDelDia.MEDIODIA && ctx.tipoDeDia == TipoDeDia.LABORABLE },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Necesitas energía rápida",
                            descripcion = "Explora menús ejecutivos o ensaladas proteicas que llegan en <20 min.",
                            etiquetas = listOf("rápido", "ligero")
                        )
                    }
                ),
                ReglaContextual(
                    nombre = "Tarde creativa",
                    condicion = { ctx -> ctx.momentoDelDia == MomentoDelDia.TARDE },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Dale un twist a la merienda",
                            descripcion = "Combina bowls frutales con cafés de especialidad o bubble tea.",
                            etiquetas = listOf("snack", "creativo")
                        )
                    }
                ),
                ReglaContextual(
                    nombre = "Clima lluvioso",
                    condicion = { ctx -> ctx.clima?.estado == EstadoClima.LLUVIA },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Que la lluvia no te frene",
                            descripcion = "Sopas asiáticas, ramen o curry especiado para entrar en calor.",
                            etiquetas = listOf("caliente", "spicy")
                        )
                    }
                ),
                ReglaContextual(
                    nombre = "Frío intenso",
                    condicion = { ctx -> ctx.clima?.estado == EstadoClima.FRIO },
                    generar = {
                        SugerenciaContextual(
                            titulo = "Activa tu termostato",
                            descripcion = "Guisos, fondue y postres calientes para reconfortar el cuerpo.",
                            etiquetas = listOf("invierno", "hogareno")
                        )
                    }
                )
            )
            return MotorRecomendacionesContextuales(reglas)
        }
    }
}
